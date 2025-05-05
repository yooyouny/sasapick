package com.sparta.product.domain.model;

import lombok.Getter;

@Getter
public enum StockEventType {
    ORDER("주문"),
    RETURN("반품"),
    IMPORT("입고"),
    EXPORT("출고");
    
    private final String displayName;
    
    StockEventType(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
