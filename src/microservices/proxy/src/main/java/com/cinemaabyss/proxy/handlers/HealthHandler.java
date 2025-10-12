package com.cinemaabyss.proxy.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handler for /health endpoint
 */
public class HealthHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(HealthHandler.class);
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.debug("Health check requested");
        
        String response = "{\"status\":\"healthy\",\"service\":\"proxy\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
