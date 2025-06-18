package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    // 根据消息ID查询消息
    @Select("SELECT * FROM chat_message WHERE message_id = #{messageId}")
    ChatMessage selectByMessageId(@Param("messageId") Long messageId);

    @Select("SELECT MAX(message_id) FROM chat_message")
    Long selectByAllMessage();

    // 根据会话ID查询所有消息
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId}")
    List<ChatMessage> selectBySessionId(@Param("sessionId") String sessionId);

    // 根据发送人ID查询消息
    @Select("SELECT * FROM chat_message WHERE send_user_id = #{sendUserId}")
    List<ChatMessage> selectBySendUserId(@Param("sendUserId") String sendUserId);

    @Select("SELECT * FROM chat_message WHERE contact_id = #{contactId}")
    List<ChatMessage> selectByContactId(@Param("contactId") String contactId);


    // 获取最近的消息（按发送时间降序排序）
    @Select("SELECT * FROM chat_message ORDER BY send_time DESC LIMIT #{limit}")
    List<ChatMessage> selectRecentMessages(@Param("limit") Integer limit);

    @Select("SELECT * FROM chat_message where contact_id = #{contactId} and send_time >= #{time}")
    List<ChatMessage> selectOffMessages(@Param("contactId") String contactId, @Param("time") Long time);


    // 插入消息
    @Insert("INSERT INTO chat_message (session_id, message_type, message_content, send_user_id, send_user_nick_name, send_time, contact_id, contact_type, file_size, file_name, file_type, status) " +
            "VALUES (#{sessionId}, #{messageType}, #{messageContent}, #{sendUserId}, #{sendUserNickName}, #{sendTime}, #{contactId}, #{contactType}, #{fileSize}, #{fileName}, #{fileType}, #{status})")
    void insertMessage(ChatMessage chatMessage);

    // 更新消息状态
    @Update("UPDATE chat_message SET status = #{status} WHERE message_id = #{messageId}")
    void updateMessageStatus(@Param("messageId") Long messageId, @Param("status") Integer status);

    // 根据消息ID删除消息
    @Delete("DELETE FROM chat_message WHERE message_id = #{messageId}")
    void deleteByMessageId(@Param("messageId") Long messageId);
}
