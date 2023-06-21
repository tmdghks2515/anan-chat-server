package io.klovers.server.domains.chat.controllers;


import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.chat.models.dtos.MessageDto;
import io.klovers.server.domains.chat.models.dtos.ReqGetChatDto;
import io.klovers.server.domains.chat.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.chat.services.ChatService;
import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

/*    @MessageMapping("/send")
    @SendToUser("/queue/receive-message") // Send the message to the user's specific queue
    public MessageDto sendMessage(@Payload ReqMsgSendDto reqDto, UserDto userDto) {
        // Process the received message
        // Perform any necessary business logic, database operations, etc.
        // You can also broadcast the message to all connected clients if needed
        reqDto.setSenderUsername(userDto.getUsername());
        return chatService.send(reqDto);
    }*/

    @PostMapping("/get")
    public ChatDto getChat(@RequestBody ReqGetChatDto dto, UserDto userDto) {
        List<String> participants = dto.getParticipants();
        participants.add(userDto.getUsername());
        return chatService.getChat(participants);
    }
}
