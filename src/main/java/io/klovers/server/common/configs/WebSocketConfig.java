package io.klovers.server.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.klovers.server.domains.chat.models.dtos.req.ReqMsgSendDto;
import io.klovers.server.domains.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.*;

import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.util.Objects.isNull;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final Map<Long, Set<WebSocketSession>> sessionsMap = new HashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("*********************** afterConnectionEstablished **************************");
                addSession(session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                log.info("*********************** handleMessage **************************");
                if (isNull(session.getPrincipal()) || isEmpty(session.getPrincipal().getName()))
                    return;
                ReqMsgSendDto msgDto = objectMapper.readValue(Objects.toString(message.getPayload()), ReqMsgSendDto.class);
                msgDto.setSenderUsername(session.getPrincipal().getName());

                WebSocketMessage<String> resultMessage = new TextMessage(objectMapper.writeValueAsString(chatService.send(msgDto)));

                // 해당 하는 socket sessions 에게 메시지 발송
                Set<WebSocketSession> sessions = sessionsMap.get(msgDto.getChatId());
                for (WebSocketSession participantSession : sessions)
                    participantSession.sendMessage(resultMessage);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.info("*********************** handleTransportError **************************");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("*********************** afterConnectionClosed **************************");
                removeSession(session);
            }

            @Override
            public boolean supportsPartialMessages() {
                log.info("*********************** supportsPartialMessages **************************");
                return false;
            }
        }, "/api/websocket/**").setAllowedOrigins("*");
    }

    private void addSession(WebSocketSession session) {
        Long chatId = getChatIdFromSession(session);
        Set<WebSocketSession> prevSessions = sessionsMap.get(chatId);

        // chatId 에 해당하는 sessions 가 없는 경우 새로 만들어줌
        if (prevSessions == null) {
            Set<WebSocketSession> sessions = new HashSet<>();
            sessions.add(session);
            sessionsMap.put(chatId, sessions);
        }
        // chatId 에 해당하는 sessions 가 이미 존재하는 경우 add session
        else {
            prevSessions.add(session);
            sessionsMap.put(chatId, prevSessions);
        }
    }

    private void removeSession(WebSocketSession session) {
        Long chatId = getChatIdFromSession(session);
        Set<WebSocketSession> prevSessions = sessionsMap.get(chatId);

        // chatId 에 해당하는 sessions 가 존재할시 해당 session remove
        if (prevSessions != null && !prevSessions.isEmpty()) {
            prevSessions.remove(session);
        }
    }

    private Long getChatIdFromSession(WebSocketSession session) {
        String path = Objects
                .requireNonNull(session.getUri())
                .getPath();

        return Long
                .parseLong(
                        path.substring(path.lastIndexOf("/") + 1)
                );
    }
}
