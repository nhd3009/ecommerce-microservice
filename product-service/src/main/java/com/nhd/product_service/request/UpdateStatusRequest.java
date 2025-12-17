package com.nhd.product_service.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusRequest {
    private String status;
}
