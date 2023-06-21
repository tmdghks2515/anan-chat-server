package io.klovers.server.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.klovers.server.domains.chat.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.util.Objects.isNull;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("*********************** afterConnectionEstablished **************************");
                if (isNull(session.getPrincipal()) || isEmpty(session.getPrincipal().getName()))
                    return;
                if (isNull(getSession(session.getPrincipal().getName())))
                    addSession(session.getPrincipal().getName(), session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                log.info("*********************** handleMessage **************************");
                if (isNull(session.getPrincipal()) || isEmpty(session.getPrincipal().getName()))
                    return;
                ReqMsgSendDto msgDto = objectMapper.readValue(Objects.toString(message.getPayload()), ReqMsgSendDto.class);
                msgDto.setSenderUsername(session.getPrincipal().getName());

                WebSocketMessage<String> resultMessage = new TextMessage(objectMapper.writeValueAsString(chatService.send(msgDto)));
                session.sendMessage(resultMessage);

//                WebSocketSession recipientSession = getSession(msgDto.getRecipientUsername());
//                recipientSession.sendMessage(resultMessage);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.info("*********************** handleTransportError **************************");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("*********************** afterConnectionClosed **************************");
                if (isNull(session.getPrincipal()) || isEmpty(session.getPrincipal().getName()))
                    return;
                removeSession(session.getPrincipal().getName());
            }

            @Override
            public boolean supportsPartialMessages() {
                log.info("*********************** supportsPartialMessages **************************");
                return false;
            }
        }, "/websocket").setAllowedOrigins("*");
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }
}
