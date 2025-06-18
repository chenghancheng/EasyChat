package com.example.easychat.service;

import com.example.easychat.entity.po.ChatSessionUser;

import java.util.List;

public interface ChatSessionUserService {

    // 根据用户ID查询所有会话
    List<ChatSessionUser> getSessionsByUserId(String userId);

    // 根据会话ID查询会话用户信息
    List<ChatSessionUser> getSessionUsersBySessionId(String sessionId);

    // 根据用户ID和联系人ID查询会话用户信息
    ChatSessionUser getSessionUser(String userId, String contactId);

    // 插入会话用户信息
    void addSessionUser(ChatSessionUser chatSessionUser);

    // 删除会话用户
    void deleteSessionUser(String userId, String contactId);

    // 更新联系人名称
    void updateContactName(String userId, String contactId, String contactName);
}
