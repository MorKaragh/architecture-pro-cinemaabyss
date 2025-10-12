package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentEvent {
    @JsonProperty("payment_id")
    private Long paymentId;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private Double amount;
    private String status;
    private String timestamp;
    
    @JsonProperty("method_type")
    private String methodType;

    public PaymentEvent() {}

    public PaymentEvent(Long paymentId, Long userId, Double amount, String status, String timestamp, String methodType) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
        this.methodType = methodType;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
