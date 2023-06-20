package io.klovers.server.domains.message.services;


import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.domains.message.models.dtos.MessageDto;
import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.message.models.entities.Message;
import io.klovers.server.domains.message.repositories.MessageRepo;
import io.klovers.server.domains.user.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final MessageRepo messageRepo;
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
                        .recipient(
                                userRepo
                                    .findById(reqDto.getRecipientId())
                                    .orElseThrow(() -> new ApiException("존재하지 않는 수신자 입니다."))
                        )
                        .content(reqDto.getContent())
                        .build()
        ).toDto();
    }
}
