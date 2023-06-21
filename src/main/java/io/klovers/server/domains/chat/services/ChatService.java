package io.klovers.server.domains.chat.services;

import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.ReqMsgSendDto;

import java.util.List;

public interface ChatService {
    MessageDto send(ReqMsgSendDto reqDto);
    ChatDto getChat(List<String> participants);
}
