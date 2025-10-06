package com.cinemaabyss.events.service;

import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventProducerService.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public EventProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    private static final String MOVIE_EVENTS_TOPIC = "movie-events";
    private static final String USER_EVENTS_TOPIC = "user-events";
    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    
    public void publishMovieEvent(MovieEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(MOVIE_EVENTS_TOPIC, eventJson);
            logger.info("Published movie event: {}", eventJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing movie event", e);
        }
    }
    
    public void publishUserEvent(UserEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(USER_EVENTS_TOPIC, eventJson);
            logger.info("Published user event: {}", eventJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing user event", e);
        }
    }
    
    public void publishPaymentEvent(PaymentEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, eventJson);
            logger.info("Published payment event: {}", eventJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing payment event", e);
        }
    }
}
