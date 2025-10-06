package com.cinemaabyss.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Load balancer for routing requests between monolith and microservice
 * Based on migration percentage configuration
 */
public class LoadBalancer {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancer.class);
    
    private final Configuration config;
    private final Random random;
    
    public LoadBalancer(Configuration config) {
        this.config = config;
        this.random = new Random();
    }
    
    /**
     * Determines which service should handle the request
     * @return URL of the target service
     */
    public String getTargetUrl() {
        int randomValue = random.nextInt(100);
        if (randomValue < config.getMigrationPercent()) {
            logger.debug("Routing to movies service (random: {}, threshold: {})", 
                        randomValue, config.getMigrationPercent());
            return config.getMoviesServiceUrl();
        } else {
            logger.debug("Routing to monolith (random: {}, threshold: {})", 
                        randomValue, config.getMigrationPercent());
            return config.getMonolithUrl();
        }
    }
    
    /**
     * Builds the full target URL with path and query parameters
     * @param baseUrl base URL of the target service
     * @param path request path
     * @param query query parameters
     * @return complete target URL
     */
    public String buildTargetUrl(String baseUrl, String path, String query) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append(path);
        if (query != null && !query.isEmpty()) {
            url.append("?").append(query);
        }
        return url.toString();
    }
}
