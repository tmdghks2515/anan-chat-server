package io.klovers.server.common.utils;

import io.klovers.server.common.models.dtos.SocketSessionDto;
import io.klovers.server.domains.chat.models.dtos.req.ReqGetMessagesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WebSocketUtils {
    private final Map<Long, Set<SocketSessionDto>> sessionsMap = new HashMap<>();

    public void addSession(WebSocketSession session) {
        Long chatId = getChatIdFromSession(session);
        Set<SocketSessionDto> prevSessions = sessionsMap.get(chatId);

        // chatId 에 해당하는 sessions 가 없는 경우 새로 만들어줌
        if (prevSessions == null) {
            Set<SocketSessionDto> sessions = new HashSet<>();
            sessions.add(
                    SocketSessionDto.builder()
                            .username(getUsernameFromSession(session))
                            .session(session)
                            .build()
            );
            sessionsMap.put(chatId, sessions);
        }
        // chatId 에 해당하는 sessions 가 이미 존재하는 경우 add session
        else {
            prevSessions.add(
                    SocketSessionDto.builder()
                            .username(getUsernameFromSession(session))
                            .session(session)
                            .build()
            );
            sessionsMap.put(chatId, prevSessions);
        }
    }

    public void removeSession(WebSocketSession session) {
        Long chatId = getChatIdFromSession(session);
        Set<SocketSessionDto> prevSessions = sessionsMap.get(chatId);

        // chatId 에 해당하는 sessions 가 존재할시 해당 session remove
        if (prevSessions != null && !prevSessions.isEmpty()) {
            prevSessions
                    .removeIf(sessionDto ->
                            sessionDto
                                    .getSession()
                                    .getId()
                                    .equals(session.getId())
                    );
        }
    }

    public Long getChatIdFromSession(WebSocketSession session) {
        String path = Objects
                .requireNonNull(session.getUri())
                .getPath();

        return Long
                .parseLong(
                        path.substring(path.lastIndexOf("/") + 1)
                );
    }

    public String getUsernameFromSession(WebSocketSession session) {
        String path = Objects
                .requireNonNull(session.getUri())
                .getPath();
        path = path.substring(0, path.lastIndexOf("/"));
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public void setTargetLang(ReqGetMessagesDto reqDto) {
        Set<SocketSessionDto> socketSessionDtos = sessionsMap.get(reqDto.getChatId());
        for (SocketSessionDto sessionDto : socketSessionDtos) {
            if (sessionDto.getUsername().equals(reqDto.getParticipantUsername())) {
                sessionDto.setTargetLang(reqDto.getTargetLang());
                break;
            }
        }
    }

    public  Set<SocketSessionDto> getSessionsByChatId(Long chatId) {
        return sessionsMap.get(chatId);
    }
}
