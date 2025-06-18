package com.example.easychat.controller;


import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.po.Avatar;
import com.example.easychat.entity.po.ChatMessage;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.service.AvatarService;
import com.example.easychat.service.ChatMessageService;
import com.example.easychat.utils.StringTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;


@RequestMapping("/file")
@RestController
public class UploadController extends ABaseController{

    @Value("${pro.uploadPath}")
    private String uploadPath;

    @Resource
    private AvatarService avatarService;

    @Resource
    private ChatMessageService chatMessageService;


    public String getUploadPath(HttpServletRequest request, String fileName) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + uploadPath + "/" + fileName;
    }


    @RequestMapping("/uploadAvatar")
    public ResponseVO uploadAvatar(HttpServletRequest request,@RequestParam("file") MultipartFile multipartFile) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String userId = tokenUserInfoDto.getUserId();
        String filePath = fileUpload(multipartFile, request);
        String groupId = null;
        if (StringTools.isEmpty(groupId)) {
            String dbAvatar = this.avatarService.getAvatar(userId);
            if (StringTools.isEmpty(dbAvatar)) {
                Avatar avatar = new Avatar();
                avatar.setAvatarUrl(filePath);
                avatar.setId(userId);
                this.avatarService.insertAvatar(avatar);
            } else {
                dbAvatar = filePath;
                Avatar avatar = new Avatar();
                avatar.setId(userId);
                avatar.setAvatarUrl(dbAvatar);
                this.avatarService.updateAvatar(avatar);
            }
        } else {
            String dbAvatar = this.avatarService.getAvatar(groupId);
            if (StringTools.isEmpty(dbAvatar)) {
                Avatar avatar = new Avatar();
                avatar.setAvatarUrl(filePath);
                avatar.setId(groupId);
                this.avatarService.insertAvatar(avatar);
            } else {
                dbAvatar = filePath;
                Avatar avatar = new Avatar();
                avatar.setId(groupId);
                avatar.setAvatarUrl(dbAvatar);
                this.avatarService.updateAvatar(avatar);
            }
        }
        System.out.println(filePath);
        return getSuccessResponseVO(filePath);
    }


    @RequestMapping("/getAvatar")
    public ResponseVO getAvatar(@RequestBody Map<String, String> map) {
        System.out.println("请求头像中.................");
        String userId = map.get("userId");
        String groupId = map.get("groupId");
        String filePath = "";
        if (!StringTools.isEmpty(userId)) {
            filePath = this.avatarService.getAvatar(userId);
        } else {
            filePath = this.avatarService.getAvatar(groupId);
        }
        return getSuccessResponseVO(filePath);
    }


    private String fileUpload(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {

        if (multipartFile.isEmpty()) {
            return "空文件";
        }

        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        if ("".equals(fileName)) {
            return "文件名不能为空";
        }
        System.out.println("上传文件原始的名字: " + fileName);

        // 使用uuid生成新文件名
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."), fileName.length());
        System.out.println("保存的文件的新名字: " + newFileName);

        // 获取年月日的日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());

        // 目标路径为 /opt/files/images，日期格式用于创建子目录
        File readPath = new File("/opt/files/images" + File.separator + format);
        System.out.println("存放的文件夹: " + readPath);
        System.out.println("存放文件的绝对路径: " + readPath.getAbsolutePath());

        // 判断文件夹是否存在
        if (!readPath.exists()) {
            // 创建文件夹
            readPath.mkdirs();
        }

        // 文件真实的保存路径
        File file = new File(readPath.getAbsolutePath() + File.separator + newFileName);
        String filePath = null;
        try {
            multipartFile.transferTo(file); // 保存文件到指定目录
            // 获取存储路径（假设你有一个方法来生成访问路径）
            filePath = getUploadPath(request, format + "/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 返回文件访问的 URL 路径
        return "http://10.29.61.159:5050/images/" + format + "/" + newFileName;
    }

    @RequestMapping ("/uploadAudio")
    public ResponseVO uploadAudio(HttpServletRequest request, @RequestParam("file") MultipartFile audioFile, @RequestParam("sessionId") String sessionId, @RequestParam("contactId") String contactId) {
        System.out.println("接受录音");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String filePath = fileUpload(audioFile, request);

        System.out.println(filePath);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageContent(filePath);
        chatMessage.setMessageType(2);
        chatMessage.setFileType(1);
        chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, tokenUserInfoDto);

        System.out.println(messageSendDto);

        return getSuccessResponseVO(messageSendDto);
    }


}
