package com.prolink.messaging.service;

import com.prolink.messaging.model.Message;
import com.prolink.messaging.repository.MessageRepository;
import com.prolink.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setUsername("sender");

        receiver = new User();
        receiver.setUsername("receiver");

        message1 = new Message(1L, sender, receiver, "Hello!", LocalDateTime.now());
        message2 = new Message(2L, receiver, sender, "Hi!", LocalDateTime.now());
    }

    @Test
    void testGetMessagesBetweenUsers() {
        // Mock the repository call
        when(messageRepository.findBySenderAndReceiverOrReceiverAndSender(sender, receiver, receiver, sender))
                .thenReturn(Arrays.asList(message1, message2));

        // Call the service method
        List<Message> messages = messageService.getMessagesBetweenUsers(sender, receiver);

        // Validate the result
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals(message1.getContent(), messages.get(0).getContent());
        assertEquals(message2.getContent(), messages.get(1).getContent());

        // Verify that the repository method was called with correct arguments
        verify(messageRepository, times(1)).findBySenderAndReceiverOrReceiverAndSender(sender, receiver, receiver, sender);
    }

    @Test
    void testSendMessage() {
        // Arrange the message to be sent
        Message newMessage = new Message(null, sender, receiver, "Hey!", LocalDateTime.now());

        // Mock the repository call
        when(messageRepository.save(newMessage)).thenReturn(newMessage);

        // Call the service method
        Message savedMessage = messageService.sendMessage(newMessage);

        // Validate that the saved message is the same as the new message
        assertNotNull(savedMessage);
        assertEquals("Hey!", savedMessage.getContent());
        assertEquals(sender, savedMessage.getSender());
        assertEquals(receiver, savedMessage.getReceiver());

        // Verify that the repository method was called to save the message
        verify(messageRepository, times(1)).save(newMessage);
    }
}
