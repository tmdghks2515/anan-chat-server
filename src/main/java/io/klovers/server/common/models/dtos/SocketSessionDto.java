package io.klovers.server.common.models.dtos;

import io.klovers.server.common.codes.Language;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketSessionDto {
    private Language targetLang;
    private String username;
    private WebSocketSession session;
}
