package com.example.easychat.service.impl;

import com.example.easychat.entity.po.ChatSession;
import com.example.easychat.mapper.ChatSessionMapper;
import com.example.easychat.service.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Override
    public ChatSession getSessionById(String sessionId) {
        return chatSessionMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<ChatSession> getAllSessions() {
        return chatSessionMapper.selectAllSessions();
    }

    @Override
    public List<ChatSession> getRecentSessions(Integer limit) {
        return chatSessionMapper.selectRecentSessions(limit);
    }

    @Override
    public void createSession(ChatSession chatSession) {
        chatSessionMapper.insert(chatSession);
    }

    @Override
    public void updateSession(ChatSession chatSession) {
        chatSessionMapper.updateSession(chatSession);
    }

    @Override
    public void deleteSession(String sessionId) {
        chatSessionMapper.deleteBySessionId(sessionId);
    }
}
