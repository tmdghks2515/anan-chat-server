package io.klovers.server.domains.chat.services;


import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.common.models.dtos.ListResDto;
import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.req.ReqMsgSendDto;
import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.chat.models.entities.Message;
import io.klovers.server.domains.chat.repositories.ChatRepo;
import io.klovers.server.domains.chat.repositories.MessageRepo;
import io.klovers.server.domains.user.models.dtos.UserDto;
import io.klovers.server.domains.user.models.entities.User;
import io.klovers.server.domains.user.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public Long getChatId(List<String> participantUsernames) {
        List<User> participants = userRepo.findAllById(participantUsernames);

        /* 해당 멤버로 구성된 채팅방이 이미 존재하는지 조회 */
        List<Long> chatIds = chatRepo.findIdsByParticipantsIn(participantUsernames, participantUsernames.size());

        if (chatIds.size() > 0) {
            return chatIds.get(0);
        } else {
            return chatRepo.save(
                    Chat.builder()
                            .participants(participants)
                            .build()
            ).getId();
        }
    }

    @Override
    public ChatDto readChat(Long chatId, UserDto userDto) {
        Chat chat = chatRepo
                .findById(chatId)
                .orElseThrow(() -> new ApiException("존재하지 않는 채팅방 입니다."));

        boolean isParticipant = chat
                .getParticipants()
                .stream()
                .anyMatch(participant -> participant.getUsername().equals(userDto.getUsername()));

        if (!isParticipant)
            throw new ApiException("해당 채팅방의 참가자가 아닙니다.");

        return chat.toDto();
    }

    @Override
    public ListResDto<MessageDto> getMessages(Long chatId, Pageable pageable, UserDto userDto) {
        Chat chat = chatRepo
                .findById(chatId)
                .orElseThrow(() -> new ApiException("존재하지 않는 채팅방 입니다."));

        if ( // api 를 요청한 유저가 채팅 참가자가 아닌 경우
                chat
                .getParticipants()
                .stream()
                .noneMatch(participant -> participant.getUsername().equals(userDto.getUsername()))
        )
            throw new ApiException("해당 채팅방의 참가자가 아닙니다.");

        Page<Message> page = messageRepo.findAllByChatId(chatId, pageable);

        return ListResDto.<MessageDto>builder()
                .list(
                        page.getContent()
                                .stream()
                                .map(Message::toDto)
                                .collect(Collectors.toList())
                                .stream()
                                .sorted(Comparator.comparing(MessageDto::getRegTs))
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public List<ChatDto> getMyChats(String username) {
        User participant = userRepo
                .findById(username)
                .orElseThrow(() -> new ApiException("존재하지 않는 회원 입니다."));

        return chatRepo
                .findByParticipantsOrderByModTsDesc(participant)
                .stream()
                .map(Chat::toDto)
                .collect(Collectors.toList());
    }
}
