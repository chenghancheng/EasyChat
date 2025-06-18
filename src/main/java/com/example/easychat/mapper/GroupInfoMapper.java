package com.example.easychat.mapper;

import com.example.easychat.entity.po.GroupInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupInfoMapper {

    // 插入群信息
    @Insert("INSERT INTO group_info (group_id, group_name, group_owner_name, create_time, group_notice, status) " +
            "VALUES (#{groupId}, #{groupName}, #{groupOwnerName}, #{createTime}, #{groupNotice}, #{status})")
    int insertGroup(GroupInfo groupInfo);

    // 根据群ID查询群信息
    @Select("SELECT * FROM group_info WHERE group_id = #{groupId}")
    GroupInfo selectGroupById(String groupId);

    // 根据群名称模糊搜索
    @Select("SELECT * FROM group_info WHERE group_name LIKE CONCAT('%', #{groupName}, '%')")
    List<GroupInfo> searchGroupsByName(String groupName);

    // 查询所有群信息
    @Select("SELECT * FROM group_info")
    List<GroupInfo> selectAllGroups();

    // 分页查询群信息
    @Select("SELECT * FROM group_info LIMIT #{offset}, #{limit}")
    List<GroupInfo> selectGroupsByPage(@Param("offset") int offset, @Param("limit") int limit);

    // 更新群信息
    @Update("UPDATE group_info SET group_name = #{groupName}, group_owner_name = #{groupOwnerName}, " +
            "create_time = #{createTime}, group_notice = #{groupNotice}, status = #{status} " +
            "WHERE group_id = #{groupId}")
    int updateGroup(GroupInfo groupInfo);

    // 删除群信息
    @Delete("DELETE FROM group_info WHERE group_id = #{groupId}")
    int deleteGroup(String groupId);
    
    
    //退出群聊
    @Delete("DELETE FROM group_member WHERE group_id = #{groupId} AND user_id = #{userId}")
    void exitGroup(@Param("groupId") String groupId, @Param("userId")String userId);
    
}
