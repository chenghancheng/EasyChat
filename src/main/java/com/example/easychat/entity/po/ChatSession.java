package com.example.easychat.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("chat_session")
public class ChatSession implements Serializable {

    @TableId(value = "session_id")
    private String sessionId;

    @TableField("last_message")
    private String lastMessage;

    @TableField("last_receive_time")
    private Long lastReceiveTime;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    @Override
    public String toString() {
        return "ChatSession{" +
                "sessionId='" + sessionId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastReceiveTime=" + lastReceiveTime +
                '}';
    }
}
