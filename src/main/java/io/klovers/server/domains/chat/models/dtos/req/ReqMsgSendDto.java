package io.klovers.server.domains.chat.models.dtos.req;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqMsgSendDto {
    private String content;
    private String senderUsername;
    private Long chatId;
}
