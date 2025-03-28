package com.prolink.messaging.controller;

import com.prolink.messaging.model.Message;
import com.prolink.messaging.model.MessageRequest;
import com.prolink.messaging.repository.MessageRepository;
import com.prolink.messaging.service.MessageService;
import com.prolink.user.model.User;
import com.prolink.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private User sender;
    private User receiver;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks here

        sender = new User();
        sender.setUsername("sender");
        
        receiver = new User();
        receiver.setUsername("receiver");

        sender.setConnections(List.of(receiver)); // Making sure they are connected
        receiver.setConnections(List.of(sender));

        message1 = new Message(1L, sender, receiver, "Hello!", LocalDateTime.now());
        message2 = new Message(2L, receiver, sender, "Hey!", LocalDateTime.now());
    }


    @Test
    void testGetMessagesBetweenConnectedUsers() {
        when(userService.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(userService.findByUsername("receiver")).thenReturn(Optional.of(receiver));
        when(messageRepository.findMessagesBetweenUsers(sender, receiver)).thenReturn(Arrays.asList(message1, message2));

        ResponseEntity<List<Message>> response = messageController.getMessages("sender", "receiver");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetMessagesUserNotFound() {
        when(userService.findByUsername("sender")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                messageController.getMessages("sender", "receiver"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testSendMessageSuccessfully() {
        MessageRequest messageRequest = new MessageRequest("sender", "receiver", "Hello!");

        when(userService.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(userService.findByUsername("receiver")).thenReturn(Optional.of(receiver));

        Message newMessage = new Message(null, sender, receiver, "Hello!", LocalDateTime.now());
        when(messageRepository.save(any(Message.class))).thenReturn(newMessage);

        ResponseEntity<?> response = messageController.sendMessage(messageRequest);

        assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    void testSendMessageFailsWhenSenderNotFound() {
        MessageRequest messageRequest = new MessageRequest("sender", "receiver", "Hello!");

        // Mocking UserService to return empty Optional for sender
        when(userService.findByUsername("sender")).thenReturn(Optional.empty());

        // Run the method and assert the exception
        Exception exception = assertThrows(RuntimeException.class, () ->
                messageController.sendMessage(messageRequest));

        // Check that the exception message is as expected
        assertEquals("Sender not found", exception.getMessage());
    }


    @Test
    void testSendMessageFailsWhenReceiverNotFound() {
        MessageRequest messageRequest = new MessageRequest("sender", "receiver", "Hello!");

        when(userService.findByUsername("sender")).thenReturn(Optional.of(sender));
        when(userService.findByUsername("receiver")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                messageController.sendMessage(messageRequest));

        assertEquals("Receiver not found", exception.getMessage());
    }
}
