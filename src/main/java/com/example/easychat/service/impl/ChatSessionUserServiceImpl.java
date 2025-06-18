package com.example.easychat.service.impl;

import com.example.easychat.entity.po.ChatSessionUser;
import com.example.easychat.mapper.ChatSessionUserMapper;
import com.example.easychat.service.ChatSessionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatSessionUserServiceImpl implements ChatSessionUserService {

    @Autowired
    private ChatSessionUserMapper chatSessionUserMapper;

    @Override
    public List<ChatSessionUser> getSessionsByUserId(String userId) {
        return chatSessionUserMapper.selectByUserId(userId);
    }

    @Override
    public List<ChatSessionUser> getSessionUsersBySessionId(String sessionId) {
        return chatSessionUserMapper.selectBySessionId(sessionId);
    }

    @Override
    public ChatSessionUser getSessionUser(String userId, String contactId) {
        return chatSessionUserMapper.selectByUserIdAndContactId(userId, contactId);
    }

    @Override
    public void addSessionUser(ChatSessionUser chatSessionUser) {
        chatSessionUserMapper.insert(chatSessionUser);
    }

    @Override
    public void deleteSessionUser(String userId, String contactId) {
        chatSessionUserMapper.deleteByUserIdAndContactId(userId, contactId);
    }

    @Override
    public void updateContactName(String userId, String contactId, String contactName) {
        chatSessionUserMapper.updateContactName(userId, contactId, contactName);
    }
}
