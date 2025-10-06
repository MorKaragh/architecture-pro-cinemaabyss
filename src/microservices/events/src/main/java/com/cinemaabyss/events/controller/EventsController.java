package com.cinemaabyss.events.controller;

import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import com.cinemaabyss.events.service.EventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventsController {
    
    private final EventProducerService eventProducerService;
    
    public EventsController(EventProducerService eventProducerService) {
        this.eventProducerService = eventProducerService;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/movie")
    public ResponseEntity<Map<String, String>> createMovieEvent(@RequestBody MovieEvent event) {
        // Set timestamp if not provided
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now().toString());
        }
        
        // Publish event to Kafka
        eventProducerService.publishMovieEvent(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/user")
    public ResponseEntity<Map<String, String>> createUserEvent(@RequestBody UserEvent event) {
        // Set timestamp if not provided
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now().toString());
        }
        
        // Publish event to Kafka
        eventProducerService.publishUserEvent(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> createPaymentEvent(@RequestBody PaymentEvent event) {
        // Set timestamp if not provided
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now().toString());
        }
        
        // Publish event to Kafka
        eventProducerService.publishPaymentEvent(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
