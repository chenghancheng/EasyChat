package com.example.easychat.service.impl;

import com.example.easychat.entity.constants.Constants;
import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.dto.UserContactSearchResultDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.enums.UserContactTypeEnum;
import com.example.easychat.entity.po.GroupInfo;
import com.example.easychat.entity.po.UserContact;
import com.example.easychat.entity.po.UserContactApply;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.GroupInfoMapper;
import com.example.easychat.mapper.UserContactApplyMapper;
import com.example.easychat.mapper.UserContactMapper;
import com.example.easychat.mapper.UserInfoMapper;
import com.example.easychat.service.UserContactApplyService;
import com.example.easychat.service.UserContactService;
import com.example.easychat.utils.CopyTools;
import com.example.easychat.websocket.ChannelContextUtils;
import com.example.easychat.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserContactServiceImpl implements UserContactService {

    @Autowired
    private UserContactMapper userContactMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private GroupInfoMapper groupInfoMapper;

    @Resource
    private UserContactApplyMapper userContactApplyMapper;

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Resource
    private MessageHandler messageHandler;

    @Override
    public List<UserContact> getContactsByUserId(String userId) {
        return userContactMapper.selectByUserId(userId);
    }

    @Override
    public UserContact getContactByUserIdAndContactId(String userId, String contactId) {
        return userContactMapper.selectByUserIdAndContactId(userId, contactId);
    }

    @Override
    public List<UserContact> getContactsByContactId(String contactId) {
        return userContactMapper.selectByContactId(contactId);
    }

    @Override
    public List<UserContact> getContactsByStatus(String userId, Integer status) {
        return userContactMapper.selectByStatus(userId, status);
    }

    @Override
    public List<UserContact> getUserGroups(String userId) {
        return userContactMapper.selectUserGroups(userId);
    }

    @Override
    public List<UserContact> getUserFriends(String userId) {
        return userContactMapper.selectUserFriends(userId);
    }

    @Override
    public boolean addUserContact(UserContact userContact) {
        try {
            userContactMapper.insertUserContact(userContact);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateContactStatus(String userId, String contactId, Integer status, String lastUpdateTime) {
        try {
            userContactMapper.updateContactStatus(userId, contactId, status, lastUpdateTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteContact(String userId, String contactId) {
        try {
            userContactMapper.deleteContact(userId, contactId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public UserContactSearchResultDto searchContact(String userId, String contactId) {
        UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (typeEnum == null) {
            return null;
        }
        UserContactSearchResultDto userContactSearchResultDto = new UserContactSearchResultDto();
        switch (typeEnum) {
            case USER:
                UserInfo userInfo = userInfoMapper.selectByUserId(userId);
                if (userInfo == null) {
                    return null;
                }
                userContactSearchResultDto = CopyTools.copy(userInfo, UserContactSearchResultDto.class);
                break;
            case GROUP:
                GroupInfo groupInfo = groupInfoMapper.selectGroupById(contactId);
                if (groupInfo == null) {
                    return null;
                }
                userContactSearchResultDto.setNickName(groupInfo.getGroupName());
                break;
        }
        userContactSearchResultDto.setContactType(typeEnum.toString());
        userContactSearchResultDto.setContactId(contactId);
        if (userId == contactId) {
            userContactSearchResultDto.setStatus(1);
            return userContactSearchResultDto;
        }

        UserContact userContact = this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
        userContactSearchResultDto.setStatus(userContact==null?null: userContact.getStatus());
        return userContactSearchResultDto;
    }




    @Override
    public void addUserContact4Robot(String userId) {
        Date curDate = new Date();

    }

}
