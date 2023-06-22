package io.klovers.server.domains.chat.services;

import io.klovers.server.common.models.dtos.ListResDto;
import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.req.ReqMsgSendDto;
import io.klovers.server.domains.user.models.dtos.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    MessageDto send(ReqMsgSendDto reqDto);
    ChatDto getChat(List<String> participants);
    ChatDto readChat(Long chatId, UserDto userDto);
    ListResDto<MessageDto> getMessages(Long chatId, Pageable pageable, UserDto userDto);
    List<ChatDto> getMyChats(String username);
}
