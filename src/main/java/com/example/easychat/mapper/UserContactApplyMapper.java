package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.UserContactApply;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserContactApplyMapper extends BaseMapper<UserContactApply> {

    // 根据 applyId 查询申请
    @Select("SELECT * FROM user_contact_apply WHERE apply_id = #{applyId}")
    UserContactApply selectByApplyId(@Param("applyId") Integer applyId);

    // 根据接收人 ID 查询所有申请
    @Select("SELECT * FROM user_contact_apply WHERE receive_user_id = #{receiveUserId}")
    List<UserContactApply> selectByReceiveUserId(@Param("receiveUserId") String receiveUserId);

    // 根据申请人 ID 查询所有申请
    @Select("SELECT * FROM user_contact_apply WHERE apply_user_id = #{applyUserId}")
    List<UserContactApply> selectByApplyUserId(@Param("applyUserId") String applyUserId);

    // 根据申请人 ID 和接收人 ID 查询所有申请
    @Select("SELECT * FROM user_contact_apply WHERE apply_user_id = #{applyUserId} AND receive_user_id = #{receiveUserId}")
    UserContactApply selectByApplyUserAndReceiver(@Param("applyUserId") String applyUserId,
                                                        @Param("receiveUserId") String receiveUserId);

    // 查询特定状态的申请总数
    @Select("SELECT COUNT(*) FROM user_contact_apply WHERE receive_user_id = #{receiveUserId} AND status = #{status}")
    Integer countByStatus(@Param("receiveUserId") String receiveUserId, @Param("status") Integer status);

    // 查询最近的申请记录
    @Select("SELECT * FROM user_contact_apply WHERE apply_user_id = #{applyUserId} ORDER BY last_apply_time DESC LIMIT #{limit}")
    List<UserContactApply> selectRecentAppliesByUserId(@Param("applyUserId") String applyUserId, @Param("limit") Integer limit);

    // 插入一条新的申请记录
    @Insert("INSERT INTO user_contact_apply (apply_user_id, receive_user_id, contact_type, contact_id, apply_info, last_apply_time, status) " +
            "VALUES (#{applyUserId}, #{receiveUserId}, #{contactType}, #{contactId}, #{applyInfo}, #{lastApplyTime}, #{status})")
    void insertUserContactApply(UserContactApply userContactApply);

    @Update({
            "<script>",
            "UPDATE user_contact_apply",
            "<set>",
            "<if test='applyUserId != null'>apply_user_id = #{applyUserId},</if>",
            "<if test='receiveUserId != null'>receive_user_id = #{receiveUserId},</if>",
            "<if test='contactType != null'>contact_type = #{contactType},</if>",
            "<if test='contactId != null'>contact_id = #{contactId},</if>",
            "<if test='lastApplyTime != null'>last_apply_time = #{lastApplyTime},</if>",
            "<if test='status != null'>status = #{status},</if>",
            "<if test='applyInfo != null'>apply_info = #{applyInfo},</if>",
            "</set>",
            "WHERE apply_id = #{applyId}",
            "</script>"
    })
    void updateUserContactApply(UserContactApply userContactApply);
    // 更新申请状态
    @Update("UPDATE user_contact_apply SET status = #{status} WHERE apply_id = #{applyId}")
    void updateStatus(@Param("applyId") Integer applyId, @Param("status") Integer status);

    @Delete("DELETE FROM user_contact_apply WHERE apply_id = #{applyId}")
    void deleteByApplyId(@Param("applyId") Integer applyId);
}
