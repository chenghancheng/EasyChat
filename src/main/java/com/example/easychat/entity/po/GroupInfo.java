package com.example.easychat.entity.po;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.util.List;

//@TableName("group_info")
public class GroupInfo {

//    @TableId(value = "group_id")
    private String groupId;          // 群id
//    @TableField("group_name")
    private String groupName;        // 群名称
//    @TableField("group_owner_name")
    private String groupOwnerName;   // 群主名称
//    @TableField("create_time")
    private Date createTime;         // 创建时间
//    @TableField("group_notice")
    private String groupNotice;      // 群公告
//    @TableField("status")
    private Byte status;              // 状态 1：正常 0：解散/封禁

    private List<UserInfo> userList;

    private String avatarUrl;

    // Getter 和 Setter 方法


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupOwnerName() {
        return groupOwnerName;
    }

    public void setGroupOwnerName(String groupOwnerName) {
        this.groupOwnerName = groupOwnerName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
