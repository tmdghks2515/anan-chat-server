package io.klovers.server.domains.message.models.dtos;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReqMsgSendDto {
    private String content;
    private String senderUsername;
    private String recipientUsername;
}
