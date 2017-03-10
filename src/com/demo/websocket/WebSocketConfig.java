package com.demo.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration  
@EnableWebSocket  
public class WebSocketConfig implements WebSocketConfigurer {  
  
    @Override  
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {  
    	registry.addHandler(myhandler(), "/websocket").addInterceptors(myInterceptors());  
        registry.addHandler(myhandler(), "/sockjs/websocket").addInterceptors(myInterceptors()).withSockJS();  
    }  
  
    @Bean  
    public WebSocketHandler myhandler() {  
        return (WebSocketHandler) new WebsocketEndPoint();  
    }  
  
    @Bean  
    public HandshakeInterceptor myInterceptors() {  
        return (HandshakeInterceptor) new com.demo.websocket.HandshakeInterceptor();  
    }  
}  