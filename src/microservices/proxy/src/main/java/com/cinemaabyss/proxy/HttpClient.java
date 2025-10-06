package com.cinemaabyss.proxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.Executors;

/**
 * HTTP client for forwarding requests to target services
 */
public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    
    private final CloseableHttpClient httpClient;
    
    public HttpClient() {
        this.httpClient = HttpClients.createDefault();
    }
    
    /**
     * Forwards HTTP request to target service
     * @param exchange original HTTP exchange
     * @param targetUrl target service URL
     * @param method HTTP method
     * @throws IOException if request fails
     */
    public void forwardRequest(HttpExchange exchange, String targetUrl, String method) throws IOException {
        try {
            org.apache.hc.client5.http.classic.methods.HttpUriRequestBase request = createRequest(method, targetUrl);
            
            // Copy headers (exclude problematic headers)
            exchange.getRequestHeaders().forEach((name, values) -> {
                if (!name.equalsIgnoreCase("host") && 
                    !name.equalsIgnoreCase("transfer-encoding") &&
                    !name.equalsIgnoreCase("content-length")) {
                    values.forEach(value -> request.setHeader(name, value));
                }
            });
            
            // Copy request body for POST/PUT
            if (method.equals("POST") || method.equals("PUT")) {
                String requestBody = readRequestBody(exchange);
                if (requestBody != null && !requestBody.isEmpty()) {
                    request.setEntity(new StringEntity(requestBody));
                }
            }
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Copy response headers (exclude problematic headers)
                for (org.apache.hc.core5.http.Header header : response.getHeaders()) {
                    if (!header.getName().equalsIgnoreCase("transfer-encoding") &&
                        !header.getName().equalsIgnoreCase("content-encoding")) {
                        exchange.getResponseHeaders().set(header.getName(), header.getValue());
                    }
                }
                
                // Copy response body
                String responseBody = EntityUtils.toString(response.getEntity());
                exchange.sendResponseHeaders(response.getCode(), responseBody.length());
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBody.getBytes());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error forwarding request to {}", targetUrl, e);
            sendErrorResponse(exchange, 502, "Bad Gateway");
        }
    }
    
    /**
     * Creates appropriate HTTP request based on method
     */
    private org.apache.hc.client5.http.classic.methods.HttpUriRequestBase createRequest(String method, String targetUrl) {
        switch (method) {
            case "GET":
                return new HttpGet(targetUrl);
            case "POST":
                return new HttpPost(targetUrl);
            case "PUT":
                return new HttpPut(targetUrl);
            case "DELETE":
                return new HttpDelete(targetUrl);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
    
    /**
     * Reads request body from HTTP exchange
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    /**
     * Sends error response
     */
    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
    
    /**
     * Closes the HTTP client
     */
    public void close() throws IOException {
        httpClient.close();
    }
}
