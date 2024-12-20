package com.example.demo.model;
public enum FilterOperation {
    MORE_THAN,
    LESS_THAN,
    EQUAL;

    public static FilterOperation fromString(String operation) {
        try {
            return FilterOperation.valueOf(operation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid filter operation: " + operation);
        }
    }
}