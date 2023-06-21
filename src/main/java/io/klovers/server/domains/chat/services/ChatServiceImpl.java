package io.klovers.server.domains.chat.services;


import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.chat.models.entities.Message;
import io.klovers.server.domains.chat.repositories.ChatRepo;
import io.klovers.server.domains.chat.repositories.MessageRepo;
import io.klovers.server.domains.user.models.dtos.UserDto;
import io.klovers.server.domains.user.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;
    private final UserRepo userRepo;

    @Override
    public MessageDto send(ReqMsgSendDto reqDto) {
        return messageRepo.save(
                Message.builder()
                        .sender(
                                userRepo
                                    .findByUsername(reqDto.getSenderUsername())
                                    .orElseThrow(() -> new ApiException("존재하지 않는 발신자 입니다."))
                        )
                        .chat(
                                chatRepo
                                    .findById(reqDto.getChatId())
                                    .orElseThrow(() -> new ApiException("존재하지 않는 채팅방 입니다."))
                        )
                        .content(reqDto.getContent())
                        .build()
        ).toDto();
    }

    @Override
    public ChatDto getChat(List<UserDto> participants) {

        return null;
    }
}
