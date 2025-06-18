package com.example.easychat.service;

import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatMessageService {

    // 根据消息ID获取消息
    ChatMessage getMessageById(Long messageId);

    // 根据会话ID获取所有消息
    List<ChatMessage> getMessagesBySessionId(String sessionId);

    // 根据发送人ID获取消息
    List<ChatMessage> getMessagesBySendUserId(String sendUserId);

    // 获取最新的消息
    List<ChatMessage> getRecentMessages(Integer limit);

    // 添加新消息
    void addMessage(ChatMessage chatMessage);

    // 更新消息状态
    void updateMessageStatus(Long messageId, Integer status);

    // 删除消息
    void deleteMessage(Long messageId);


    MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto);

    void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover);
}
