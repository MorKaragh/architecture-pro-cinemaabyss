package com.cinemaabyss.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventConsumerService.class);
    
    private final ObjectMapper objectMapper;
    
    public EventConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "movie-events", groupId = "events-service-group")
    public void consumeMovieEvent(String message) {
        logger.info("Consumed movie event: {}", message);
        // Here you can add additional processing logic
        // For now, we just log the event
    }
    
    @KafkaListener(topics = "user-events", groupId = "events-service-group")
    public void consumeUserEvent(String message) {
        logger.info("Consumed user event: {}", message);
        // Here you can add additional processing logic
        // For now, we just log the event
    }
    
    @KafkaListener(topics = "payment-events", groupId = "events-service-group")
    public void consumePaymentEvent(String message) {
        logger.info("Consumed payment event: {}", message);
        // Here you can add additional processing logic
        // For now, we just log the event
    }
}
