package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.ChatSession;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    // 根据会话ID查询会话
    @Select("SELECT * FROM chat_session WHERE session_id = #{sessionId}")
    ChatSession selectBySessionId(@Param("sessionId") String sessionId);

    // 查询所有会话
    @Select("SELECT * FROM chat_session")
    List<ChatSession> selectAllSessions();

    // 根据最后接收消息时间排序查询会话
    @Select("SELECT * FROM chat_session ORDER BY last_receive_time DESC LIMIT #{limit}")
    List<ChatSession> selectRecentSessions(@Param("limit") Integer limit);

    // 插入新会话记录
    @Insert("INSERT INTO chat_session (session_id, last_message, last_receive_time) " +
            "VALUES (#{sessionId}, #{lastMessage}, #{lastReceiveTime})")
    void insertChatSession(ChatSession chatSession);

    // 更新会话的最后消息和接收时间
    @Update("UPDATE chat_session SET last_message = #{lastMessage}, last_receive_time = #{lastReceiveTime} " +
            "WHERE session_id = #{sessionId}")
    void updateLastMessageAndTime(@Param("sessionId") String sessionId,
                                  @Param("lastMessage") String lastMessage,
                                  @Param("lastReceiveTime") Long lastReceiveTime);

    @Update("UPDATE chat_session SET last_message = #{lastMessage}, last_receive_time = #{lastReceiveTime} where session_id = #{sessionId}")
    void updateSession(ChatSession chatSession);




    // 删除指定会话
    @Delete("DELETE FROM chat_session WHERE session_id = #{sessionId}")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    void removeUserFromSession(@Param("groupId") String groupId, @Param("userId") String userId);

}
