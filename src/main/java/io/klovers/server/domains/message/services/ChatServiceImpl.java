package io.klovers.server.domains.message.services;


import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.message.models.entities.Message;
import io.klovers.server.domains.message.repositories.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final MessageRepo messageRepo;

    @Override
    public void send(ReqMsgSendDto reqDto) {
        messageRepo.save(
                Message.builder()
                        .content(reqDto.getContent())
                        .build()
        );
    }
}
