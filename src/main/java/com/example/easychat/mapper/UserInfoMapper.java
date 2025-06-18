package com.example.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.easychat.entity.po.UserInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    // 通过 UserId 查询用户
    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo selectByUserId(@Param("userId") String userId);

    // 通过 email 查询用户
    @Select("SELECT * FROM user_info WHERE email = #{email}")
    UserInfo selectByEmail(String email);

    // 查询所有用户
    @Select("SELECT * FROM user_info")
    List<UserInfo> selectAllUsers();

    // 通过状态查询用户
    @Select("SELECT * FROM user_info WHERE status = #{status}")
    List<UserInfo> selectByStatus(@Param("status") Integer status);

    // 通过昵称模糊查询用户
    @Select("SELECT * FROM user_info WHERE nick_name LIKE CONCAT('%', #{nickName}, '%')")
    List<UserInfo> selectByNickName(@Param("nickName") String nickName);

    // 查询最近上线的用户
    @Select("SELECT * FROM user_info ORDER BY last_join_time DESC LIMIT #{limit}")
    List<UserInfo> selectRecentUsers(@Param("limit") Integer limit);

    // 查询特定地区的用户
    @Select("SELECT * FROM user_info WHERE area_name = #{areaName}")
    List<UserInfo> selectByAreaName(@Param("areaName") String areaName);

    // 通过性别和状态查询用户
    @Select("SELECT * FROM user_info WHERE sex = #{sex} AND status = #{status}")
    List<UserInfo> selectBySexAndStatus(@Param("sex") Integer sex, @Param("status") Integer status);

    // 统计用户总数
    @Select("SELECT COUNT(*) FROM user_info")
    Integer countAllUsers();

    // 按状态统计用户数量
    @Select("SELECT COUNT(*) FROM user_info WHERE status = #{status}")
    Integer countByStatus(@Param("status") Integer status);

    @Insert("INSERT INTO user_info (user_id, email, nick_name, password, create_time, status, personal_signature) " +
            "VALUES (#{userId}, #{email}, #{nickName}, #{password}, #{createTime}, #{status}, #{personalSignature})")
    void insertUserInfo(UserInfo userInfo);



    @Update("<script>" +
            "UPDATE user_info " +
            "<set>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='nickName != null'>nick_name = #{nickName},</if>" +
            "<if test='password != null'>password = #{password},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='lastLoginTime != null'>last_login_time = #{lastLoginTime},</if>" +
            "</set> " +
            "WHERE user_id = #{userId}" +
            "</script>")
    void updateUserInfoById(@Param("userId") String userId,
                            @Param("email") String   email,
                            @Param("nickName") String nickName,
                            @Param("password") String password,
                            @Param("status") Integer status,
                            @Param("lastLoginTime") Date lastLoginTime);

    @Update("<script>" +
            "UPDATE user_info " +
            "<set>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='nickName != null'>nick_name = #{nickName},</if>" +
            "<if test='password != null'>password = #{password},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='sex != null'>sex = #{sex},</if>"+
            "<if test='personalSignature != null'>personal_signature = #{personalSignature},</if>"+
            "<if test='lastLoginTime != null'>last_login_time = #{lastLoginTime},</if>" +
            "<if test='lastOffTime != null'>last_off_time = #{lastOffTime},</if>"+
            "</set> " +
            "WHERE user_id = #{userId}" +
            "</script>")
    void updateUserInfo(UserInfo userInfo);


}
