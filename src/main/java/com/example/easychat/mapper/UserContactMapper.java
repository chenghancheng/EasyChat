package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.UserContact;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserContactMapper extends BaseMapper<UserContact> {

    // 通过用户ID查询联系人列表
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId}")
    List<UserContact> selectByUserId(@Param("userId") String userId);

    // 通过用户ID和联系人ID查询联系记录
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId} AND contact_id = #{contactId}")
    UserContact selectByUserIdAndContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    // 根据联系人ID查询所有的联系人记录
    @Select("SELECT * FROM user_contact WHERE contact_id = #{contactId}")
    List<UserContact> selectByContactId(@Param("contactId") String contactId);

    // 根据状态查询用户的联系人列表
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId} AND status = #{status}")
    List<UserContact> selectByStatus(@Param("userId") String userId, @Param("status") Integer status);

    // 获取用户的所有群组
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId} AND contact_type = 1")
    List<UserContact> selectUserGroups(@Param("userId") String userId);

    // 获取用户的所有好友
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId} AND contact_type = 0")
    List<UserContact> selectUserFriends(@Param("userId") String userId);

    // 插入联系人记录
    @Insert("INSERT INTO user_contact (user_id, contact_id, contact_type, create_time, status, last_update_time) " +
            "VALUES (#{userId}, #{contactId}, #{contactType}, #{createTime}, #{status}, #{lastUpdateTime})")
    void insertUserContact(UserContact userContact);

    // 更新联系人状态
    @Update("UPDATE user_contact SET status = #{status}, last_update_time = #{lastUpdateTime} " +
            "WHERE user_id = #{userId} AND contact_id = #{contactId}")
    void updateContactStatus(@Param("userId") String userId, @Param("contactId") String contactId,
                             @Param("status") Integer status, @Param("lastUpdateTime") String lastUpdateTime);

    // 删除联系人记录
    @Delete("DELETE FROM user_contact WHERE user_id = #{userId} AND contact_id = #{contactId}")
    void deleteContact(@Param("userId") String userId, @Param("contactId") String contactId);

    // 根据用户ID和联系人类型查询用户的特定类型联系人
    @Select("SELECT * FROM user_contact WHERE user_id = #{userId} AND contact_type = #{contactType}")
    List<UserContact> selectByUserIdAndContactType(@Param("userId") String userId, @Param("contactType") Integer contactType);
}
