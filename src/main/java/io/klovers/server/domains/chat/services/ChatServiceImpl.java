package io.klovers.server.domains.chat.services;


import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.chat.models.entities.Message;
import io.klovers.server.domains.chat.repositories.ChatRepo;
import io.klovers.server.domains.chat.repositories.MessageRepo;
import io.klovers.server.domains.user.models.entities.User;
import io.klovers.server.domains.user.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public ChatDto getChat(List<String> participantUsernames) {
        List<User> participants = userRepo.findAllById(participantUsernames);

        /* 해당 멤버로 구성된 채팅방이 이미 존재하는지 조회 */
        List<Chat> chats = chatRepo.findByParticipantsIn(participants);

        if (chats.size() > 0) {
            return chats.get(0).toDto();
        } else {
            return chatRepo.save(
                    Chat.builder()
                            .participants(participants)
                            .build()
            ).toDto();
        }
    }
}
