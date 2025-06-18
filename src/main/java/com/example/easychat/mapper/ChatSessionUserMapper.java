package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.ChatSessionUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ChatSessionUserMapper extends BaseMapper<ChatSessionUser> {

    // 根据用户ID查询其所有会话
    @Select("SELECT * FROM chat_session_user WHERE user_id = #{userId}")
    List<ChatSessionUser> selectByUserId(@Param("userId") String userId);

    // 根据会话ID查询相关用户
    @Select("SELECT * FROM chat_session_user WHERE session_id = #{sessionId}")
    List<ChatSessionUser> selectBySessionId(@Param("sessionId") String sessionId);

    // 根据用户ID和联系人ID查询会话
    @Select("SELECT * FROM chat_session_user WHERE user_id = #{userId} AND contact_id = #{contactId}")
    ChatSessionUser selectByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    // 插入新会话用户记录
    @Insert("INSERT INTO chat_session_user (user_id, contact_id, session_id, contact_name) " +
            "VALUES (#{userId}, #{contactId}, #{sessionId}, #{contactName})")
    void insertChatSessionUser(ChatSessionUser chatSessionUser);

    // 更新会话的联系人名称
    @Update("UPDATE chat_session_user SET contact_name = #{contactName} " +
            "WHERE user_id = #{userId} AND contact_id = #{contactId}")
    void updateContactName(@Param("userId") String userId, @Param("contactId") String contactId, @Param("contactName") String contactName);

    // 删除会话用户记录
    @Delete("DELETE FROM chat_session_user WHERE user_id = #{userId} AND contact_id = #{contactId}")
    void deleteByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    // 根据会话ID删除所有相关记录
    @Delete("DELETE FROM chat_session_user WHERE session_id = #{sessionId}")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    @Update("UPDATE chat_session_user SET contact_name = #{contactName} " +
            "WHERE contact_id = #{contactId}")
    void updateContactNameByContactId(@Param("contactId") String contactId, @Param("contactName") String contactName);
}
