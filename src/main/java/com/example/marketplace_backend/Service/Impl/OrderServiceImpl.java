package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.DTO.Responses.models.OrderResponse;
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

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            StatusRepository statusRepository,
                            CartService cartService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
        this.cartService = cartService;
    }

    public OrderResponse createOrderFromCart(UUID userId, OrderRequest request) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<UUID> selectedItemIds = request.getCartItemIds();
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            throw new RuntimeException("No cart items selected");
        }

        Order order = new Order();
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        order.setAddress(request.getAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setComment(request.getComment());
        order.setWholesale(request.getIsWholesale());

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

        // Удаляем выбранные товары из корзины
        cart.getCartItems().removeIf(item -> selectedItemIds.contains(item.getId()));
        cartRepository.save(cart);

        return OrderMapper.toOrderResponse(savedOrder);
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

    public Page<Order> getOrdersByStatus(UUID statusId, Pageable pageable) {
        return orderRepository.findByStatusesId(statusId, pageable);
    }

    public Page<Order> getOrdersByStatusName(String statusName, Pageable pageable) {
        return orderRepository.findByStatusesName(statusName, pageable);
    }

    // Методы для работы со статусами
    public Order addStatusToOrder(UUID orderId, UUID statusId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Statuses status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        // Создаем новый статус для заказа
        Statuses orderStatus = Statuses.builder()
                .name(status.getName())
                .color(status.getColor())
                .order(order)
                .build();

        if (order.getStatuses() == null) {
            order.setStatuses(new ArrayList<>());
        }
        order.getStatuses().add(orderStatus);

        return orderRepository.save(order);
    }

    public Order addStatusToOrderByName(UUID orderId, String statusName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Statuses status = statusRepository.findByName(statusName);

        // Создаем новый статус для заказа
        Statuses orderStatus = Statuses.builder()
                .name(status.getName())
                .color(status.getColor())
                .order(order)
                .build();

        if (order.getStatuses() == null) {
            order.setStatuses(new ArrayList<>());
        }
        order.getStatuses().add(orderStatus);

        return orderRepository.save(order);
    }

    public Order removeStatusFromOrder(UUID orderId, UUID statusId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatuses() != null) {
            order.getStatuses().removeIf(status -> status.getId().equals(statusId));
        }

        return orderRepository.save(order);
    }

    public List<Statuses> getOrderStatuses(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getStatuses();
    }

    // Методы для получения заказов по статусу без пагинации
    public List<Order> getOrdersByStatusName(String statusName) {
        return orderRepository.findByStatusesName(statusName);
    }

    public List<Order> getOrdersWithStatus(UUID statusId) {
        Statuses status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        return orderRepository.findByStatusesName(status.getName());
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
}