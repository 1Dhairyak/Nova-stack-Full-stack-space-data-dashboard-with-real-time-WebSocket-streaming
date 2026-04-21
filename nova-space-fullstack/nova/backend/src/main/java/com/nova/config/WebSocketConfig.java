package com.nova.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * Registers the STOMP endpoints the client will connect to via SockJS.
     * /ws is the HTTP handshake endpoint; SockJS is the fallback transport.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    /**
     * Configures the message broker:
     *  - /topic  → broadcast (one-to-many, e.g. ISS position updates)
     *  - /queue  → point-to-point (one user, e.g. personal alerts)
     *  - /app    → prefix for @MessageMapping controller methods
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Plugs the JWT channel interceptor so every WebSocket CONNECT
     * frame is authenticated before the connection is established.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketAuthChannelInterceptor());
    }
}
