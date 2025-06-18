package com.example.easychat.service;

import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.dto.UserContactSearchResultDto;
import com.example.easychat.entity.po.UserContact;

import java.util.List;

public interface UserContactService {

    // 根据用户ID查询所有联系人
    List<UserContact> getContactsByUserId(String userId);

    // 根据用户ID和联系人ID查询联系人
    UserContact getContactByUserIdAndContactId(String userId, String contactId);

    // 根据联系人ID查询所有的联系记录
    List<UserContact> getContactsByContactId(String contactId);

    // 根据状态查询某个用户的联系人
    List<UserContact> getContactsByStatus(String userId, Integer status);

    // 获取用户的所有群组
    List<UserContact> getUserGroups(String userId);

    // 获取用户的所有好友
    List<UserContact> getUserFriends(String userId);

    // 插入联系人记录
    boolean addUserContact(UserContact userContact);

    // 更新联系人状态
    boolean updateContactStatus(String userId, String contactId, Integer status, String lastUpdateTime);

    // 删除联系人记录
    boolean deleteContact(String userId, String contactId);


    UserContactSearchResultDto searchContact(String userId, String contactId);


    void addUserContact4Robot(String userId);
}
