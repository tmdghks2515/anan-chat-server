package io.klovers.server.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.klovers.server.common.codes.Language;
import io.klovers.server.common.models.dtos.SocketSessionDto;
import io.klovers.server.common.papago.PapagoTranslationService;
import io.klovers.server.common.utils.WebSocketUtils;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.req.ReqGetMessagesDto;
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
    private final ChatService chatService;
    private final PapagoTranslationService papagoTranslationService;
    private final WebSocketUtils socketUtils;
    private final ObjectMapper objectMapper;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("*********************** afterConnectionEstablished **************************");
                socketUtils.addSession(session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                log.info("*********************** handleMessage **************************");
                if (isNull(session.getPrincipal()) || isEmpty(session.getPrincipal().getName()))
                    return;
                ReqMsgSendDto sendDto = objectMapper.readValue(Objects.toString(message.getPayload()), ReqMsgSendDto.class);
                sendDto.setSenderUsername(session.getPrincipal().getName());

                MessageDto messageDto = chatService.send(sendDto);

                // 해당 하는 socket sessions 에게 메시지 발송
                Set<SocketSessionDto> sessions = socketUtils.getSessionsByChatId(sendDto.getChatId());
                for (SocketSessionDto sessionDto : sessions) {
                    // 번역 후 전송
                    if (sessionDto.getTargetLang() != null) {
                        MessageDto translatedMsg = MessageDto.builder()
                                .id(messageDto.getId())
                                .content(
                                        papagoTranslationService.translateText(
                                                messageDto.getContent(),
                                                sessionDto.getTargetLang().name()
                                        )
                                )
                                .sender(messageDto.getSender())
                                .regTs(messageDto.getRegTs())
                                .modTs(messageDto.getModTs())
                                .build();
                        sessionDto
                                .getSession()
                                .sendMessage(new TextMessage(objectMapper.writeValueAsString(translatedMsg)));
                    }
                    // 번역 하지 않고 전송
                    else {
                        sessionDto
                                .getSession()
                                .sendMessage(new TextMessage(objectMapper.writeValueAsString(messageDto)));
                    }
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.info("*********************** handleTransportError **************************");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("*********************** afterConnectionClosed **************************");
                socketUtils.removeSession(session);
            }

            @Override
            public boolean supportsPartialMessages() {
                log.info("*********************** supportsPartialMessages **************************");
                return false;
            }
        }, "/api/websocket/**").setAllowedOrigins("*");
    }

}
