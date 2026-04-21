package com.nova.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // FIX: inject as Spring bean — NOT `new WebSocketAuthChannelInterceptor()`
    // The original code used `new`, which bypassed Spring's DI container entirely,
    // meaning @Autowired fields inside the interceptor were always null → JWT auth
    // on WebSocket silently failed for every single connection.
    private final WebSocketAuthChannelInterceptor authChannelInterceptor;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // FIX: use the injected bean, not a raw `new` instance
        registration.interceptors(authChannelInterceptor);
    }
}
