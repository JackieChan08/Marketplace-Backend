package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
import com.example.marketplace_backend.DTO.Responses.models.OrderWholesaleResponse;
import com.example.marketplace_backend.DTO.Responses.models.UserResponse;
import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Requests.models.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, UUID> {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StatusRepository statusRepository;
    private final CartService cartService;
    private final OrderNumberGeneratorService orderNumberGeneratorService;
    private final ConverterService converterService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            StatusRepository statusRepository,
                            CartService cartService,
                            OrderNumberGeneratorService orderNumberGeneratorService, ConverterService converterService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
        this.cartService = cartService;
        this.orderNumberGeneratorService = orderNumberGeneratorService;
        this.converterService = converterService;
    }

    public OrderResponse createOrderFromCart(UUID userId, OrderRequest request) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<UUID> selectedItemIds = request.getCartItemIds();
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            throw new RuntimeException("No cart items selected");
        }

        // Заполняем недостающие данные у пользователя
        boolean userUpdated = false;
        if ((user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) && request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
            userUpdated = true;
        }

        if ((user.getCity() == null || user.getCity().isEmpty()) && request.getCity() != null) {
            user.setCity(request.getCity());
            userUpdated = true;
        }

        if ((user.getAddress() == null || user.getAddress().isEmpty()) && request.getAddress() != null) {
            user.setAddress(request.getAddress());
            userUpdated = true;
        }

        // Сохраняем пользователя, если его данные были обновлены
        if (userUpdated) {
            userRepository.save(user);
        }

        Order order = new Order();
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setComment(request.getComment());
        order.setWholesale(false);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderNumber(orderNumberGeneratorService.generateOrderNumber());

        // Устанавливаем статус
        if (request.getStatusId() != null) {
            Statuses status = statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found with ID: " + request.getStatusId()));
            order.setStatus(status);
        } else {
            Statuses defaultStatus = statusRepository.findByNameByOrderFlag("Без статуса")
                    .orElseThrow(() -> new RuntimeException("Default status not found"));
            order.setStatus(defaultStatus);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> selectedItemIds.contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No matching cart items found");
        }

        for (CartItem cartItem : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found")));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            BigDecimal itemTotal = cartItem.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);
        return converterService.toOrderResponse(savedOrder);
    }

    public OrderWholesaleResponse createOrderWholesale(UUID userId, OrderRequest request) {
        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Заполняем недостающие данные у пользователя
        boolean userUpdated = false;
        if ((user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) && request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
            userUpdated = true;
        }

        if ((user.getAddress() == null || user.getAddress().isEmpty()) && request.getAddress() != null) {
            user.setAddress(request.getAddress());
            userUpdated = true;
        }

        // Сохраняем пользователя, если его данные были обновлены
        if (userUpdated) {
            userRepository.save(user);
        }

        // Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setComment(request.getComment());
        order.setWholesale(true);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderNumber(orderNumberGeneratorService.generateOrderNumber());

        // Устанавливаем статус
        if (request.getStatusId() != null) {
            Statuses status = statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Status not found with ID: " + request.getStatusId()));
            order.setStatus(status);
        } else {
            Statuses defaultStatus = statusRepository.findByName("Без статуса")
                    .orElseThrow(() -> new RuntimeException("Default status not found"));
            order.setStatus(defaultStatus);
        }

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        return converterService.toOrderWholesaleResponse(savedOrder);
    }


    // Методы без пагинации
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }

    public List<Order> getAllWholesaleOrders() {
        return orderRepository.findAllWholesaleOrders();
    }

    public List<Order> getAllRetailOrders() {
        return orderRepository.findAllRetailOrders();
    }

    // Методы с пагинацией
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllOrders(pageable);
    }

    public Page<Order> getAllWholesaleOrders(Pageable pageable) {
        return orderRepository.findAllWholesaleOrders(pageable);
    }

    public Page<Order> getAllRetailOrders(Pageable pageable) {
        return orderRepository.findAllRetailOrders(pageable);
    }

    // Методы для обновления заказа
    public Order updateOrderComment(UUID orderId, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setComment(comment);
        return orderRepository.save(order);
    }

    public Order updateOrderAddress(UUID orderId, String address) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setAddress(address);
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(UUID orderId, UUID statusId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Statuses status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Page<Order> findOrdersByUserId(Pageable pageable, UUID userId) {
        return orderRepository.findOrdersByUser(pageable, userId);
    }

    public Page<Order> findOrdersByStatus(Pageable pageable, UUID statusId) {
        return orderRepository.findOrdersByStatusId(pageable, statusId);
    }

    public OrderResponse findByOrderNumber(String orderNumber) {
        String normalized = orderNumber.toUpperCase();

        Order order = orderRepository.findByOrderNumber(normalized)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));

        return converterService.toOrderResponse(order);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findUsersSortedByLastOrderDateDesc();
        return users.stream()
                .map(converterService::convertToUserResponse)
                .toList();
    }
}