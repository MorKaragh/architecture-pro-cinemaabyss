package com.cinemaabyss.proxy.handlers;

import com.cinemaabyss.proxy.HttpClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Handler for /api/users endpoint - proxies to monolith service
 */
public class UsersHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UsersHandler.class);
    
    private final HttpClient httpClient;
    private final String monolithUrl;
    
    public UsersHandler(HttpClient httpClient, String monolithUrl) {
        this.httpClient = httpClient;
        this.monolithUrl = monolithUrl;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        
        logger.info("Received {} request to {}", method, path);
        
        try {
            // Build target URL for monolith service
            String targetUrl = monolithUrl + path;
            if (query != null && !query.isEmpty()) {
                targetUrl += "?" + query;
            }
            
            logger.info("Proxying users request to: {}", targetUrl);
            
            // Forward the request to monolith
            httpClient.forwardRequest(exchange, targetUrl, method);
            
        } catch (Exception e) {
            logger.error("Error handling users request", e);
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }
    }
    
    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.length());
        try (var os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
