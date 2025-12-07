package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID orderId;

    @NotBlank(message = "Адрес обязателен")
    private String address;

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Номер телефона содержит недопустимые символы")
    private String phoneNumber;

    @NotNull(message = "Необходимо указать тип заказа (оптовый/розничный)")
    private Boolean isWholesale;

    private String comment;

    @NotNull(message = "Метод оплаты обязателен")
    private PaymentMethod paymentMethod;

    @NotNull(message = "ID статуса обязателен")
    private UUID statusId;

    @NotEmpty(message = "Список товаров в корзине не может быть пустым")
    private List<UUID> cartItemIds;
}