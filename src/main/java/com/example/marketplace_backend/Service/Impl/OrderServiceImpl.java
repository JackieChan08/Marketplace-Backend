package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Requests.models.OrderRequest;
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
    private final OrderStatusRepository orderStatusRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            OrderStatusRepository orderStatusRepository,
                            CartService cartService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.cartService = cartService;
    }

    public Order createOrderFromCart(UUID userId, OrderRequest request) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Получаем дефолтный статус (например, "Pending" или "Created")
        OrderStatuses defaultStatus = orderStatusRepository.findByName("Pending")
                .orElseThrow(() -> new RuntimeException("Default order status 'Pending' not found"));

        Order order = new Order();
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        order.setAddress(request.getAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setOrderStatuses(defaultStatus);
        order.setComment(request.getComment());
        order.setWholesale(request.getIsWholesale());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
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

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }

    public Order updateOrderStatus(UUID orderId, UUID statusId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatuses orderStatus = orderStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Order status not found"));

        order.setOrderStatuses(orderStatus);
        return orderRepository.save(order);
    }

    public Order updateOrderStatusByName(UUID orderId, String statusName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatuses orderStatus = orderStatusRepository.findByName(statusName)
                .orElseThrow(() -> new RuntimeException("Order status not found: " + statusName));

        order.setOrderStatuses(orderStatus);
        return orderRepository.save(order);
    }

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

    public List<Order> getAllWholesaleOrders() {
        return orderRepository.findAllWholesaleOrders();
    }

    public List<Order> getAllRetailOrders() {
        return orderRepository.findAllRetailOrders();
    }

    public List<Order> getOrdersByStatus(UUID statusId) {
        return orderRepository.findByOrderStatusesId(statusId);
    }

    public List<Order> getOrdersByStatusName(String statusName) {
        OrderStatuses orderStatus = orderStatusRepository.findByName(statusName)
                .orElseThrow(() -> new RuntimeException("Order status not found: " + statusName));
        return orderRepository.findByOrderStatusesId(orderStatus.getId());
    }
}