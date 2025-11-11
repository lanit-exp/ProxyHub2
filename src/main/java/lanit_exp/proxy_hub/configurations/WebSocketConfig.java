package lanit_exp.proxy_hub.configurations;

import lanit_exp.proxy_hub.handlers.WSMessageInterceptor;
import lanit_exp.proxy_hub.handlers.WSSessionHandler;
import lanit_exp.proxy_hub.services.WSSessions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WSMessageInterceptor wsMessageInterceptor;
    private final WSSessions wsSessions;

    @Value("${ws.message.size_limit}")
    private Integer messageSizeLimit;
    @Value("${ws.message.send_time_limit}")
    private Integer messageSendTimeLimit;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/node");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setOrder(0);
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(wsMessageInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(wsMessageInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WSSessionHandler(handler, wsSessions));
        registry.setMessageSizeLimit(messageSizeLimit);
        registry.setSendBufferSizeLimit(messageSizeLimit);
        registry.setSendTimeLimit(messageSendTimeLimit);
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(messageSizeLimit);
        container.setMaxSessionIdleTimeout(0L);
        return container;
    }
}
