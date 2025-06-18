package com.example.easychat.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.easychat.entity.enums.UserContactTypeEnum;

import java.io.Serializable;

@TableName("chat_session_user")
public class ChatSessionUser implements Serializable {
    private static final long serialVersionUID = -882472298829182760L;

    @TableId(value = "user_id")
    private String userId;

    @TableField("contact_id")
    private String contactId;

    @TableField("session_id")
    private String sessionId;

    @TableField("contact_name")
    private String contactName;

    private String lastMessage;

    private Long lastReceiveTime;

    private Integer memberCount;

    private Integer contactType;

    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getContactType() {
        return UserContactTypeEnum.getByPrefix(contactId).getType();
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(Long lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return "ChatSessionUser{" +
                "userId='" + userId + '\'' +
                ", contactId='" + contactId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}
