package com.example.easychat.service.impl;

import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.enums.ResponseCodeEnum;
import com.example.easychat.entity.enums.UserContactTypeEnum;
import com.example.easychat.entity.po.ChatMessage;
import com.example.easychat.entity.po.ChatSession;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.ChatMessageMapper;
import com.example.easychat.mapper.ChatSessionMapper;
import com.example.easychat.mapper.ChatSessionUserMapper;
import com.example.easychat.redis.RedisComponent;
import com.example.easychat.service.ChatMessageService;
import com.example.easychat.utils.CopyTools;
import com.example.easychat.utils.StringTools;
//import com.sun.org.apache.xpath.internal.operations.String;
import com.example.easychat.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private MessageHandler messageHandler;

    @Override
    public ChatMessage getMessageById(Long messageId) {
        return chatMessageMapper.selectByMessageId(messageId);
    }

    @Override
    public List<ChatMessage> getMessagesBySessionId(String sessionId) {
        return chatMessageMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<ChatMessage> getMessagesBySendUserId(String sendUserId) {
        return chatMessageMapper.selectBySendUserId(sendUserId);  // 使用修改后的 Mapper 方法
    }

    @Override
    public List<ChatMessage> getRecentMessages(Integer limit) {
        return chatMessageMapper.selectRecentMessages(limit);  // 使用修改后的 Mapper 方法
    }

    @Override
    public void addMessage(ChatMessage chatMessage) {
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public void updateMessageStatus(Long messageId, Integer status) {
        chatMessageMapper.updateMessageStatus(messageId, status);
    }

    @Override
    public void deleteMessage(Long messageId) {
        chatMessageMapper.deleteByMessageId(messageId);
    }

    @Override
    public MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto) {
        List<String> contactList = redisComponent.getUserContactList(tokenUserInfoDto.getUserId());
        if (!contactList.contains(chatMessage.getContactId())) {
            UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatMessage.getContactId());
            if (UserContactTypeEnum.USER == userContactTypeEnum) {
                throw new BusinessException(ResponseCodeEnum.CODE_902);
            } else {
                throw new BusinessException(ResponseCodeEnum.CODE_903);
            }
        }

        String sessionId = null;
        String contactId = chatMessage.getContactId();
        String sendUserId = tokenUserInfoDto.getUserId();
        Long curTime = System.currentTimeMillis();
        UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (UserContactTypeEnum.USER == userContactTypeEnum) {
            sessionId = StringTools.getChatSessionId4User(new String[] {sendUserId, contactId});
        } else {
            sessionId = StringTools.getChatSessionId4Group(contactId);
        }

        System.out.println("..................................");
        System.out.println(sessionId);
        System.out.println("......................");
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(chatMessage.getMessageType());

        Integer status = MessageTypeEnum.MEDIA_CHAT == messageTypeEnum ? 0 : 1;
        chatMessage.setStatus(status);
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(curTime);

        String messageContent = StringTools.cleanHtmlTag(chatMessage.getMessageContent());
        chatMessage.setMessageContent(messageContent);

        //更新会话
        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        //如果是群聊 加一句
        if (UserContactTypeEnum.GROUP == userContactTypeEnum) {
            chatSession.setLastMessage(tokenUserInfoDto.getNickName() + "：" + messageContent);
        }
        if (chatMessage.getFileType() != null) {
            chatSession.setLastMessage("[语音消息]");
        }
        chatSession.setLastReceiveTime(curTime);
        if (chatMessage.getFileType() != null) {
            chatSessionMapper.updateLastMessageAndTime(sessionId,"[语音消息]",curTime);
        } else {
            chatSessionMapper.updateLastMessageAndTime(sessionId,messageContent,curTime);
        }

        //记录消息表
        chatMessage.setSendUserId(sendUserId);
        chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
        chatMessage.setContactType(userContactTypeEnum.getType());

        chatMessageMapper.insertMessage(chatMessage);
        Long messageId = this.chatMessageMapper.selectByAllMessage();
        chatMessage.setMessageId(messageId);

        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        if (chatMessage.getFileType() != null) {
            messageSendDto.setLastMessage("[语音消息]");
        }
        messageHandler.sendMessage(messageSendDto);



        return messageSendDto;
    }

    @Override
    public void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover){
        ChatMessage chatMessage = chatMessageMapper.selectByMessageId(messageId);
        if (chatMessage == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (chatMessage.getSendUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

    }
}
