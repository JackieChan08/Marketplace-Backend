package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.*;
import com.example.marketplace_backend.Model.Intermediate_objects.CartItem;
import com.example.marketplace_backend.Model.Intermediate_objects.OrderItem;
import com.example.marketplace_backend.Repositories.*;
import com.example.marketplace_backend.DTO.Requests.models.OrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

        List<UUID> selectedItemIds = request.getCartItemIds();
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            throw new RuntimeException("No cart items selected");
        }

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

        cart.getCartItems().removeIf(item -> selectedItemIds.contains(item.getId()));
        cartRepository.save(cart);

        return savedOrder;
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
        return orderRepository.findByOrderStatusesId(statusId, pageable);
    }

    public Page<Order> getOrdersByStatusName(String statusName, Pageable pageable) {
        return orderRepository.findByOrderStatusesName(statusName, pageable);
    }

}