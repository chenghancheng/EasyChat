package com.example.easychat.service;

import com.example.easychat.entity.po.GroupInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface GroupInfoService {
    boolean addGroup(GroupInfo groupInfo);

    // 根据群ID查询群
    GroupInfo getGroupById(String groupId);

    // 根据群名称搜索群
    List<GroupInfo> searchGroupsByName(String groupName);

    // 查询所有群
    List<GroupInfo> getAllGroups();

    // 分页查询群
    List<GroupInfo> getGroupsByPage(int pageNum, int pageSize);

    // 更新群信息
    boolean updateGroup(GroupInfo groupInfo);

    // 删除群
    boolean deleteGroup(String groupId);

    void saveGroup(GroupInfo groupInfo);

    void exitGroup(String groupId, String userId);

}
