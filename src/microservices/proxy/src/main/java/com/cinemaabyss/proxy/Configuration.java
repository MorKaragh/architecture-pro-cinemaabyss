package com.cinemaabyss.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for Proxy Server
 * Handles environment variables and provides default values
 */
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    
    private final int port;
    private final String monolithUrl;
    private final String moviesServiceUrl;
    private final int migrationPercent;
    
    public Configuration() {
        this.port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
        this.monolithUrl = System.getenv().getOrDefault("MONOLITH_URL", "http://localhost:8080");
        this.moviesServiceUrl = System.getenv().getOrDefault("MOVIES_SERVICE_URL", "http://localhost:8081");
        this.migrationPercent = Integer.parseInt(System.getenv().getOrDefault("MOVIES_MIGRATION_PERCENT", "50"));
        
        logger.info("Configuration loaded:");
        logger.info("Port: {}", port);
        logger.info("Monolith URL: {}", monolithUrl);
        logger.info("Movies Service URL: {}", moviesServiceUrl);
        logger.info("Migration Percent: {}%", migrationPercent);
    }
    
    public int getPort() {
        return port;
    }
    
    public String getMonolithUrl() {
        return monolithUrl;
    }
    
    public String getMoviesServiceUrl() {
        return moviesServiceUrl;
    }
    
    public int getMigrationPercent() {
        return migrationPercent;
    }
}
