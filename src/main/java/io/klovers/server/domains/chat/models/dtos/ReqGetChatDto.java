package io.klovers.server.domains.chat.models.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class ReqGetChatDto {
    private List<String> participants;
}
