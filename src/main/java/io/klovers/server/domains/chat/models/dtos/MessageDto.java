package io.klovers.server.domains.chat.models.dtos;

import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private String content;
    private UserDto sender;
    private ChatDto chat;
    private LocalDateTime regTs;
    private LocalDateTime modTs;
}
