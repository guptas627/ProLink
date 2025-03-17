package com.prolink.messaging.controller;

import com.prolink.messaging.model.Message;
import com.prolink.messaging.model.MessageRequest;
import com.prolink.messaging.repository.MessageRepository;
import com.prolink.messaging.service.MessageService;
import com.prolink.user.model.User;
import com.prolink.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
	@Autowired
    private MessageRepository messageRepository;
	@Autowired
    private UserService userService;
    private final MessageService messageService;
    

    //  Fetch messages between two users (Only if they are connections)
    @GetMapping("/{username1}/{username2}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String username1, @PathVariable String username2) {
        User user1 = userService.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userService.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages = messageRepository.findMessagesBetweenUsers(user1, user2);
        return ResponseEntity.ok(messages);
    }


    // Send a new message (Only if they are connections)
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest messageRequest) {
        User sender = userService.findByUsername(messageRequest.getSender())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userService.findByUsername(messageRequest.getReceiver())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (!sender.getConnections().contains(receiver)) {
            return ResponseEntity.badRequest().body("You can only message your connections.");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(messageRequest.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        messageRepository.save(message);
        return ResponseEntity.ok(message);
    }

}
