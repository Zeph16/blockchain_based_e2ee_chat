package md.proj.encryptedchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import md.proj.encryptedchat.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtService jwtService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000", "http://localhost:5173");
    }
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message, org.springframework.messaging.MessageChannel channel) {
                logger.info("Received message: {}", message.getPayload());
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null) {
                    String token = null;

                    List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
                    if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                        token = authorizationHeaders.get(0).replace("Bearer ", "");
                        System.out.println("Token found: " + token);
                    }

                    if (token != null && jwtService.extractClaim(token, Claims::getSubject) != null) {
                        String username = jwtService.extractClaim(token, Claims::getSubject);
                        System.out.println("Username in token: " + username);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.NO_AUTHORITIES);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);

                        // Check if the user is trying to subscribe to their own queue
                        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                            String destination = accessor.getDestination();
                            if (destination != null && destination.startsWith("/user") && !destination.startsWith("/user/" + username)) {
                                System.out.println("Username is " + username + " and destination is " + destination);
                                throw new IllegalArgumentException("User not authorized to subscribe to this queue");
                            }
                            System.out.println("Username is " + username + " and destination is " + destination + ", it is validated");
                        }

                    } else {
                        throw new IllegalStateException("Missing or invalid JWT token");
                    }
                }
                    return message;
                }

        });
    }

    @Override
    public void configureClientOutboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public void afterSendCompletion(org.springframework.messaging.Message<?> message, org.springframework.messaging.MessageChannel channel, boolean sent, Exception ex) {
                logger.info("Sent message: {}", message.getPayload());
                ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
            }
        });
    }
}