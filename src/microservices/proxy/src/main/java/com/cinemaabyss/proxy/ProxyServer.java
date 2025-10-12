package com.cinemaabyss.proxy;

import com.cinemaabyss.proxy.handlers.HealthHandler;
import com.cinemaabyss.proxy.handlers.MoviesHandler;
import com.cinemaabyss.proxy.handlers.UsersHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main Proxy Server class
 * Orchestrates all components and starts the HTTP server
 */
public class ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    
    private final Configuration config;
    private final LoadBalancer loadBalancer;
    private final HttpClient httpClient;
    private final HttpServer server;
    
    public ProxyServer() throws IOException {
        this.config = new Configuration();
        this.loadBalancer = new LoadBalancer(config);
        this.httpClient = new HttpClient();
        this.server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        
        setupRoutes();
        server.setExecutor(Executors.newFixedThreadPool(10));
    }
    
    /**
     * Sets up HTTP routes
     */
    private void setupRoutes() {
        // Movies endpoint with load balancing
        server.createContext("/api/movies", new MoviesHandler(loadBalancer, httpClient));
        
        // Users endpoint - proxy to monolith
        server.createContext("/api/users", new UsersHandler(httpClient, config.getMonolithUrl()));
        
        // Health check endpoint
        server.createContext("/health", new HealthHandler());
    }
    
    /**
     * Starts the proxy server
     */
    public void start() {
        server.start();
        logger.info("Proxy server started on port {}", config.getPort());
    }
    
    /**
     * Stops the proxy server
     */
    public void stop() {
        server.stop(0);
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("Error closing HTTP client", e);
        }
        logger.info("Proxy server stopped");
    }
    
    public static void main(String[] args) {
        try {
            ProxyServer server = new ProxyServer();
            server.start();
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down proxy server...");
                server.stop();
            }));
            
            // Keep the server running
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Failed to start proxy server", e);
            System.exit(1);
        }
    }
}
