package com.example.easychat.controller;


import com.example.easychat.config.Appconfig;
import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.po.ChatMessage;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.service.ChatMessageService;
import com.example.easychat.service.ChatSessionUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController{
    @Resource
    private ChatMessageService chatMessageService;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    Appconfig appconfig;

    @RequestMapping("/sendMessage")
    public ResponseVO sendMessage(@RequestBody Map<String, Object> sendMessageMap, HttpServletRequest request){
        System.out.println(request);
        System.out.println(sendMessageMap.toString());
        String contactId =(String) sendMessageMap.get("contactId");
        String messageContent =(String) sendMessageMap.get("messageContent");
        Integer messageType = (Integer)sendMessageMap.get("messageType");
        Long fileSize = (Long) sendMessageMap.get("fileSize");
        String fileName = (String) sendMessageMap.get("fileName");
        Integer fileType = (Integer) sendMessageMap.get("fileType");
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(messageType);
        if (messageTypeEnum == null || (messageType != 2 && messageType != 5)) {
            throw new BusinessException("消息类型错误!");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setFileSize(fileSize);
        chatMessage.setMessageType(messageType);
        chatMessage.setFileName(fileName);
        chatMessage.setFileType(fileType);


        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, tokenUserInfoDto);

        System.out.println(messageSendDto);

        return getSuccessResponseVO(messageSendDto);
    }


    @RequestMapping("uploadFile")
    private ResponseVO uploadFile(HttpServletRequest request, @NotNull Long messageId,
                                  @NotNull MultipartFile file,
                                  @NotNull MultipartFile cover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        chatMessageService.saveMessageFile(tokenUserInfoDto.getUserId(), messageId, file, cover);

        return getSuccessResponseVO(null);
    }
}
