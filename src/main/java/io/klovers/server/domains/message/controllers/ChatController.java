package io.klovers.server.domains.message.controllers;


import io.klovers.server.domains.message.models.dtos.MessageDto;
import io.klovers.server.domains.message.models.dtos.ReqMsgSendDto;
import io.klovers.server.domains.message.services.ChatService;
import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/send")
    @SendToUser("/queue/receive-message") // Send the message to the user's specific queue
    public MessageDto sendMessage(@Payload ReqMsgSendDto reqDto, UserDto userDto) {
        // Process the received message
        // Perform any necessary business logic, database operations, etc.
        // You can also broadcast the message to all connected clients if needed
        reqDto.setSenderUsername(userDto.getUsername());
        return chatService.send(reqDto);
    }
}
