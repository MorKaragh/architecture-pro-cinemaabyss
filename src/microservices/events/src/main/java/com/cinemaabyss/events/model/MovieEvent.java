package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieEvent {
    @JsonProperty("movie_id")
    private Long movieId;
    
    private String title;
    private String action;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private String timestamp;

    public MovieEvent() {}

    public MovieEvent(Long movieId, String title, String action, Long userId, String timestamp) {
        this.movieId = movieId;
        this.title = title;
        this.action = action;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
