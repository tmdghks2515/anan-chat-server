package io.klovers.server.domains.message.models.dtos;

import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private String content;
    private UserDto sender;
    private UserDto recipient;
    private LocalDateTime regTs;
    private LocalDateTime modTs;
}
