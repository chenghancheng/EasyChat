package com.example.easychat.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@TableName("user_contact_apply")
public class UserContactApply implements Serializable {

    @TableId(value = "apply_id", type = IdType.AUTO)
    private Integer applyId;

    @TableField("apply_user_id")
    private String applyUserId;

    @TableField("receive_user_id")
    private String receiveUserId;

    @TableField("contact_type")
    private Integer contactType;

    @TableField("contact_id")
    private String contactId;

    @TableField("last_apply_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastApplyTime;

    @TableField("status")
    private Integer status;

    @TableField("apply_info")
    private String applyInfo;


    private String applyUserNickName;

    private String receiveUserNickName;


    private String avatarUrl;

    // Getters and Setters


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getApplyUserNickName() {
        return applyUserNickName;
    }

    public void setApplyUserNickName(String applyUserNickName) {
        this.applyUserNickName = applyUserNickName;
    }

    public String getReceiveUserNickName() {
        return receiveUserNickName;
    }

    public void setReceiveUserNickName(String receiveUserNickName) {
        this.receiveUserNickName = receiveUserNickName;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Date getLastApplyTime() {
        return lastApplyTime;
    }

    public void setLastApplyTime(Date lastApplyTime) {
        this.lastApplyTime = lastApplyTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApplyInfo() {
        return applyInfo;
    }

    public void setApplyInfo(String applyInfo) {
        this.applyInfo = applyInfo;
    }

    @Override
    public String toString() {
        return "UserContactApply{" +
                "applyId=" + applyId +
                ", applyUserId='" + applyUserId + '\'' +
                ", receiveUserId='" + receiveUserId + '\'' +
                ", contactType=" + contactType +
                ", contactId='" + contactId + '\'' +
                ", lastApplyTime=" + lastApplyTime +
                ", status=" + status +
                ", applyInfo='" + applyInfo + '\'' +
                ", applyUserNickName='" + applyUserNickName + '\'' +
                ", receiveUserNickName='" + receiveUserNickName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
