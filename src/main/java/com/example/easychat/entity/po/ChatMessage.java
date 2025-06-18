package com.example.easychat.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("chat_message")
public class ChatMessage implements Serializable {

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    @TableField("session_id")
    private String sessionId;

    @TableField("message_type")
    private Integer messageType;

    @TableField("message_content")
    private String messageContent;

    @TableField("send_user_id")
    private String sendUserId;

    @TableField("send_user_nick_name")
    private String sendUserNickName;

    @TableField("send_time")
    private Long sendTime;

    @TableField("contact_id")
    private String contactId;

    @TableField("contact_type")
    private Integer contactType;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_name")
    private String fileName;

    @TableField("file_type")
    private Integer fileType;

    @TableField("status")
    private Integer status;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSendUserNickName() {
        return sendUserNickName;
    }

    public void setSendUserNickName(String sendUserNickName) {
        this.sendUserNickName = sendUserNickName;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId=" + messageId +
                ", sessionId='" + sessionId + '\'' +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                ", sendUserId='" + sendUserId + '\'' +
                ", sendUserNickName='" + sendUserNickName + '\'' +
                ", sendTime=" + sendTime +
                ", contactId='" + contactId + '\'' +
                ", contactType=" + contactType +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", status=" + status +
                '}';
    }
}
