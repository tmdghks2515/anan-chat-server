package io.klovers.server.domains.message.controllers;


import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.message.services.ChatService;
import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat/send")
    public void send(@RequestBody ReqMsgSendDto reqDto, UserDto userDto) {
        reqDto.setSenderId(userDto.getId());
        chatService.send(reqDto);
    }
}
