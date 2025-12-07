package com.example.marketplace_backend.DTO.Requests.models.WatchRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrapSizeRequest {
    @NotBlank(message = "Название размера ремешка не может быть пустым")
    private String name;

    @NotEmpty(message = "Список циферблатов не может быть пустым")
    @Valid
    private List<DialRequest> dialRequests;
}