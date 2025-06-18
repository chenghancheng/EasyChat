package com.example.easychat.service;

import com.example.easychat.entity.po.ChatSession;

import java.util.List;

public interface ChatSessionService {

    // 根据会话ID获取会话信息
    ChatSession getSessionById(String sessionId);

    // 获取所有会话信息
    List<ChatSession> getAllSessions();

    // 获取最近的会话
    List<ChatSession> getRecentSessions(Integer limit);

    // 创建一个新的会话
    void createSession(ChatSession chatSession);

    // 更新会话信息
    void updateSession(ChatSession chatSession);

    // 删除会话
    void deleteSession(String sessionId);
}
