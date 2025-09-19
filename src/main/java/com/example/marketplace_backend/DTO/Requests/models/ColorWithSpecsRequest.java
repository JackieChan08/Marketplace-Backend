package com.example.marketplace_backend.DTO.Requests.models;

import com.example.marketplace_backend.DTO.Requests.models.LaptopRequest.ChipRequest;
import com.example.marketplace_backend.DTO.Requests.models.PhoneSpecRequest.SimTypeRequest;
import com.example.marketplace_backend.DTO.Requests.models.TableRequest.TableModuleRequest;
import com.example.marketplace_backend.DTO.Requests.models.WatchRequest.StrapSizeRequest;
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
    private String name;
    private String hex;
    private BigDecimal price;
    private List<MultipartFile> images;
    private List<SimTypeRequest> simTypeRequests;// phoneSpec
    private List<ChipRequest> chipRequests; //laptopSpec
    private List<TableModuleRequest> tableModuleRequests; //tableSpec
    private List<StrapSizeRequest> strapSizeRequests; //watchSpec
}