package io.klovers.server.domains.message.models.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqMsgSendDto {
    private String content;
    private String senderId;
    private String recipientId;
}
