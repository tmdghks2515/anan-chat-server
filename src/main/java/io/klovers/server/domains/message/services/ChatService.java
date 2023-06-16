package io.klovers.server.domains.message.services;

import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;

public interface ChatService {
    void send(ReqMsgSendDto reqDto);
}
