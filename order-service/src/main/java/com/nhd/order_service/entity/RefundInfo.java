package com.nhd.order_service.entity;

import com.nhd.order_service.enums.RefundMethod;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RefundInfo {
    private RefundMethod method;

    private String receiver;
    private String note;
}