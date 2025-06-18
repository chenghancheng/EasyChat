package com.example.easychat.service.impl;

import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.enums.UserContactTypeEnum;
import com.example.easychat.entity.po.*;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.*;
import com.example.easychat.redis.RedisComponent;
import com.example.easychat.service.GroupInfoService;
import com.example.easychat.service.UserInfoService;
import com.example.easychat.utils.CopyTools;
import com.example.easychat.utils.StringTools;
import com.example.easychat.websocket.ChannelContextUtils;
import com.example.easychat.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class GroupInfoServiceImpl implements GroupInfoService {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @Resource
    private UserContactMapper userContactMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;


    @Resource
    private MessageHandler messageHandler;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Override
    public boolean addGroup(GroupInfo groupInfo) {
        return groupInfoMapper.insertGroup(groupInfo) > 0;
    }

    @Override
    public GroupInfo getGroupById(String groupId) {
        return groupInfoMapper.selectGroupById(groupId);
    }

    @Override
    public List<GroupInfo> searchGroupsByName(String groupName) {
        return groupInfoMapper.searchGroupsByName("%" + groupName + "%");
    }

    @Override
    public List<GroupInfo> getAllGroups() {
        return groupInfoMapper.selectAllGroups();
    }

    @Override
    public List<GroupInfo> getGroupsByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return groupInfoMapper.selectGroupsByPage(offset, pageSize);
    }

    @Override
    public boolean updateGroup(GroupInfo groupInfo) {
        return groupInfoMapper.updateGroup(groupInfo) > 0;
    }

    @Override
    public boolean deleteGroup(String groupId) {
        return groupInfoMapper.deleteGroup(groupId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(GroupInfo groupInfo) {

        if (groupInfo.getGroupId() == null) {

            Date curDate = new Date();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String randomNumber = uuid.substring(0, 10);
            groupInfo.setGroupId("G" + randomNumber);
            groupInfo.setCreateTime(curDate);
            groupInfo.setStatus((byte)1);

            this.groupInfoMapper.insertGroup(groupInfo);

            UserContact userContact = new UserContact();
            userContact.setStatus(0);
            userContact.setContactType(1);
            userContact.setContactId(groupInfo.getGroupId());
            userContact.setUserId(groupInfo.getGroupOwnerName());
            userContact.setCreateTime(curDate);
            userContact.setLastUpdateTime(curDate);
            this.userContactMapper.insertUserContact(userContact);

            //创建会话
            String session = StringTools.getChatSessionId4Group(groupInfo.getGroupId());
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(session);
            chatSession.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSession.setLastReceiveTime(System.currentTimeMillis());
            this.chatSessionMapper.insertChatSession(chatSession);

            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(groupInfo.getGroupOwnerName());
            chatSessionUser.setContactId(groupInfo.getGroupId());
            chatSessionUser.setContactName(groupInfo.getGroupName());
            chatSessionUser.setSessionId(session);
            this.chatSessionUserMapper.insertChatSessionUser(chatSessionUser);

            //创建消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(session);
            chatMessage.setMessageType(MessageTypeEnum.GROUP_CREATE.getType());
            chatMessage.setMessageContent(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatMessage.setSendTime(System.currentTimeMillis());
            chatMessage.setContactId(groupInfo.getGroupId());
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
            chatMessage.setStatus(1);
            chatMessageMapper.insertMessage(chatMessage);

            //将群组添加到联系人
            redisComponent.addUserContact(groupInfo.getGroupOwnerName(), groupInfo.getGroupId());


            //将联系人通道添加到群组通道中
            channelContextUtils.addUser2Group(groupInfo.getGroupOwnerName(), groupInfo.getGroupId());


            //发送ws消息
            chatSessionUser.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSessionUser.setLastReceiveTime(System.currentTimeMillis());
            chatSessionUser.setMemberCount(1);

            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSendDto.setExtendData(chatSessionUser);
            messageSendDto.setLastMessage(chatSessionUser.getLastMessage());

            messageHandler.sendMessage(messageSendDto);
        } else {
            GroupInfo dbInfo = this.groupInfoMapper.selectGroupById(groupInfo.getGroupId());
            if (!dbInfo.getGroupOwnerName().equals(groupInfo.getGroupOwnerName())) {
                throw new BusinessException("无权限");
            }
            this.groupInfoMapper.updateGroup(groupInfo);
            //TODO:修改群昵称 发送消息

            String contactNameUpdate = null;
            if (!dbInfo.getGroupName().equals(groupInfo.getGroupName())) {
                contactNameUpdate = groupInfo.getGroupName();
            }
            if (contactNameUpdate == null) return;

            this.chatSessionUserMapper.updateContactNameByContactId(groupInfo.getGroupId(), contactNameUpdate);

            MessageSendDto messageSendDto = new MessageSendDto();
            messageSendDto.setContactType(UserContactTypeEnum.GROUP.getType());
            messageSendDto.setContactId(groupInfo.getGroupId());
            messageSendDto.setExtendData(contactNameUpdate);
            messageSendDto.setMessageType(MessageTypeEnum.GROUP_NAME_UPDATE.getType());
            messageHandler.sendMessage(messageSendDto);

        }


    }

    public void exitGroup(String groupId, String userId) {
        
        // 从群组中移除用户
        groupInfoMapper.exitGroup(groupId, userId);
        
        // 删除用户与群组的聊天会话记录
        chatSessionMapper.removeUserFromSession(groupId, userId);
        
    }
}