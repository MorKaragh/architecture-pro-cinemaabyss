package com.cinemaabyss.proxy.handlers;

import com.cinemaabyss.proxy.HttpClient;
import com.cinemaabyss.proxy.LoadBalancer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Handler for /api/movies endpoint with load balancing
 */
public class MoviesHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoviesHandler.class);
    
    private final LoadBalancer loadBalancer;
    private final HttpClient httpClient;
    
    public MoviesHandler(LoadBalancer loadBalancer, HttpClient httpClient) {
        this.loadBalancer = loadBalancer;
        this.httpClient = httpClient;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        
        logger.info("Received {} request to {}", method, path);
        
        try {
            // Determine target URL based on load balancing
            String targetUrl = loadBalancer.getTargetUrl();
            String fullUrl = loadBalancer.buildTargetUrl(targetUrl, path, query);
            
            logger.info("Proxying request to: {}", fullUrl);
            
            // Forward the request
            httpClient.forwardRequest(exchange, fullUrl, method);
            
        } catch (Exception e) {
            logger.error("Error handling request", e);
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
