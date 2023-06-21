package io.klovers.server.domains.chat.models.dtos;

import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {
    private Long id;
    private String name;
    private List<UserDto> participants;
}
