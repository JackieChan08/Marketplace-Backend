package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.DTO.Requests.models.LaptopRequest.ChipRequest;
import com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest.SimTypeRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableModuleRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.StrapSizeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorWithSpecsRequest {
    @NotBlank(message = "Название цвета не может быть пустым")
    private String name;

    @NotBlank(message = "HEX код цвета обязателен")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Некорректный HEX код цвета")
    private String hex;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotEmpty(message = "Необходимо загрузить хотя бы одно изображение")
    private List<MultipartFile> images;

    @Valid
    private List<SimTypeRequest> simTypeRequests;

    @Valid
    private List<ChipRequest> chipRequests;

    @Valid
    private List<TableModuleRequest> tableModuleRequests;

    @Valid
    private List<StrapSizeRequest> strapSizeRequests;
}