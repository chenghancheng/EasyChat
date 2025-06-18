package com.example.easychat.entity.vo;

import com.example.easychat.entity.po.ChatSession;
import com.example.easychat.entity.po.UserInfo;

import java.io.Serializable;
import java.util.List;

public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = -72078405540385571L;
    private String userId;

    private String nickName;

    private Integer sex;

    private Integer joinType;

    private String personalSignature;

    private String token;

    private Boolean admin;

    private Integer contactStatus;

    private List<String> contactIdList;

    private List<UserInfo> contactList;

    private List<ChatSession> sessionList;

    public List<String> getContactIdList() {
        return contactIdList;
    }

    public void setContactIdList(List<String> contactIdList) {
        this.contactIdList = contactIdList;
    }

    public void setContactList(List<UserInfo> contactList) {
        this.contactList = contactList;
    }

    private List<String> contactApplyList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public List<UserInfo> getContactList() {
        return contactList;
    }

    public List<ChatSession> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<ChatSession> sessionList) {
        this.sessionList = sessionList;
    }

    public List<String> getContactApplyList() {
        return contactApplyList;
    }

    public void setContactApplyList(List<String> contactApplyList) {
        this.contactApplyList = contactApplyList;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Integer getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(Integer contactStatus) {
        this.contactStatus = contactStatus;
    }
}
