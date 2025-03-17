package com.prolink.messaging.service;

import com.prolink.messaging.model.Message;
import com.prolink.messaging.repository.MessageRepository;
import com.prolink.user.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessagesBetweenUsers(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiverOrReceiverAndSender(sender, receiver, receiver, sender);
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }
}
