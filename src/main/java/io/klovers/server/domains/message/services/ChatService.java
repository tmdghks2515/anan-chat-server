package io.klovers.server.domains.message.services;

import io.klovers.server.domains.message.models.dtos.MessageDto;
import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;

public interface ChatService {
    MessageDto send(ReqMsgSendDto reqDto);
}
