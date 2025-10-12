package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEvent {
    @JsonProperty("user_id")
    private Long userId;
    
    private String username;
    private String action;
    private String timestamp;

    public UserEvent() {}

    public UserEvent(Long userId, String username, String action, String timestamp) {
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
