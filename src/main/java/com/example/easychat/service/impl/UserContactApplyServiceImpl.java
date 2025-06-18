package com.example.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.easychat.entity.constants.Constants;
import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.enums.UserContactTypeEnum;
import com.example.easychat.entity.po.*;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.*;
import com.example.easychat.redis.RedisComponent;
import com.example.easychat.service.AvatarService;
import com.example.easychat.service.UserContactApplyService;
import com.example.easychat.utils.CopyTools;
import com.example.easychat.utils.StringTools;
import com.example.easychat.websocket.ChannelContextUtils;
import com.example.easychat.websocket.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserContactApplyServiceImpl implements UserContactApplyService {

    @Autowired
    private UserContactApplyMapper userContactApplyMapper;

    @Resource
    private UserContactMapper userContactMapper;

    @Resource
    private GroupInfoMapper groupInfoMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Resource
    private AvatarService avatarService;

    @Override
    public UserContactApply getApplyById(Integer applyId) {
        return userContactApplyMapper.selectById(applyId);
    }


    @Override
    public List<UserContactApply> getAppliesByReceiveUserId(String receiveUserId) {
        QueryWrapper<UserContactApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receive_user_id", receiveUserId);
        return userContactApplyMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserContactApply> getAppliesByApplyUserId(String applyUserId) {
        QueryWrapper<UserContactApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("apply_user_id", applyUserId);
        return userContactApplyMapper.selectList(queryWrapper);
    }

    @Override
    public UserContactApply getAppliesByApplyUserAndReceiver(String applyUserId, String receiveUserId) {
        UserContactApply userContactApply = userContactApplyMapper.selectByApplyUserAndReceiver(applyUserId, receiveUserId);
        return userContactApply;
    }

    @Override
    public void createApply(String applyUserId, String receiveUserId, Integer contactType, String contactId, String applyInfo) {
        UserContactApply apply = new UserContactApply();
        apply.setApplyUserId(applyUserId);
        apply.setReceiveUserId(receiveUserId);
        apply.setContactType(contactType);
        apply.setContactId(contactId);
        apply.setApplyInfo(applyInfo);
        apply.setLastApplyTime(new Date());
        apply.setStatus(0); // 默认状态为待处理
        userContactApplyMapper.insert(apply);
    }

    @Override
    public void updateApplyStatus(Integer applyId, Integer status) {
        UserContactApply apply = userContactApplyMapper.selectById(applyId);
        if (apply != null) {
            apply.setStatus(status);
            userContactApplyMapper.updateById(apply);
        }
    }

    @Override
    public List<UserContactApply> getRecentAppliesByUserId(String userId, Integer limit) {
        QueryWrapper<UserContactApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("apply_user_id", userId)
                .orderByDesc("last_apply_time")
                .last("LIMIT " + limit);
        return userContactApplyMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserContactApply> loadApply(String userId) {
        return userContactApplyMapper.selectByApplyUserId(userId);
    }

    @Override
    public List<UserContactApply> loadReceiveApply(String userId) {
        return userContactApplyMapper.selectByReceiveUserId(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealWithApply(String userId, String applyId, String ok) {
        //applyId -> 申请编号
        //userId -> 处理人
        System.out.println(userId);
        System.out.println(applyId);
        UserContactApply userContactApply = this.userContactApplyMapper.selectByApplyId(Integer.valueOf(applyId));
        System.out.println(userContactApply);
        if (userContactApply == null || !userId.equals(userContactApply.getReceiveUserId())) {
            throw new BusinessException("参数错误！");
        }
        String contactId = userContactApply.getReceiveUserId();
        String contactId2 = userContactApply.getContactId();

        //用flag来表示是加入群组还是加好友，当flag为true时表示加联系人，false时表示加入群组
        boolean flag = true;
        if (!contactId2.equals(contactId)) flag = false;

        //如果已经存在申请了，用这个申请去更新原申请
        UserContactApply updateInfo = new UserContactApply();
        updateInfo.setLastApplyTime(new Date());

        //对于当前接收人来说，真正的联系人是当时申请的人
        String trueContactId = userContactApply.getApplyUserId();
        if (ok.equals("1")) {
            UserContact userContact1 = new UserContact();
            UserContact userContact2 = new UserContact();
            userContact1.setUserId(userId);
            userContact1.setContactId(trueContactId);
            if (flag) userContact1.setContactType(0);
            else userContact1.setContactType(1);
            if (!flag) {
                userContact1.setUserId(userContactApply.getApplyUserId());
                userContact1.setContactId(userContactApply.getContactId());
            }

            userContact1.setCreateTime(new Date());
            userContact1.setLastUpdateTime(new Date());

            userContact2.setUserId(trueContactId);
            userContact2.setContactId(userId);
            if (flag) userContact2.setContactType(0);
            else userContact2.setContactType(1);
            if (!flag) {
                userContact2.setContactId(userContactApply.getApplyUserId());
                userContact2.setUserId(userContactApply.getContactId());
            }
            userContact2.setCreateTime(new Date());
            userContact2.setLastUpdateTime(new Date());
            System.out.println(userContact1);
            System.out.println(userContact2);
            this.userContactMapper.insertUserContact(userContact1);
            this.userContactMapper.insertUserContact(userContact2);

            //加入redis缓存
            if (UserContactTypeEnum.USER.getType().equals(userContactApply.getContactType())) {
                redisComponent.addUserContact(trueContactId, userId);
            }
            redisComponent.addUserContact(userId, trueContactId);

            System.out.println("已经加入缓存");

            //TODO:创建会话
            String sessionId = null;
            if (UserContactTypeEnum.USER.getType().equals(userContactApply.getContactType())) {
                sessionId = StringTools.getChatSessionId4User(new String[]{userId, trueContactId});
            } else {
                sessionId = StringTools.getChatSessionId4Group(userContactApply.getContactId());
            }
            System.out.println("正在创建会话。。。。。");
            if (UserContactTypeEnum.USER.getType().equals(userContactApply.getContactType())) {
                UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(trueContactId);
                UserInfo receiveUserInfo = this.userInfoMapper.selectByUserId(userId);


                //创建会话
                ChatSession chatSession = new ChatSession();
                chatSession.setSessionId(sessionId);
                chatSession.setLastMessage(userContactApply.getApplyInfo());
                chatSession.setLastReceiveTime(System.currentTimeMillis());
                chatSessionMapper.insertChatSession(chatSession);

                //接受人的session
                ChatSessionUser applyChatSessionUser = new ChatSessionUser();
                applyChatSessionUser.setContactId(trueContactId);
                applyChatSessionUser.setUserId(userId);
                applyChatSessionUser.setSessionId(sessionId);
                applyChatSessionUser.setContactName(applyUserInfo.getNickName());

                //申请人的session
                ChatSessionUser receiveChatSessionUser = new ChatSessionUser();
                receiveChatSessionUser.setContactId(userId);
                receiveChatSessionUser.setUserId(trueContactId);
                receiveChatSessionUser.setSessionId(sessionId);
                receiveChatSessionUser.setContactName(receiveUserInfo.getNickName());
                System.out.println(applyChatSessionUser);
                System.out.println(receiveChatSessionUser);


                chatSessionUserMapper.insertChatSessionUser(applyChatSessionUser);
                chatSessionUserMapper.insertChatSessionUser(receiveChatSessionUser);

                System.out.println("会话创建完毕.....");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSessionId(sessionId);
                chatMessage.setMessageType(MessageTypeEnum.ADD_FRIEND_SELF.getType());
                chatMessage.setMessageContent(userContactApply.getApplyInfo());
                chatMessage.setSendUserId(userContactApply.getApplyUserId());
                chatMessage.setSendUserNickName(userContactApply.getApplyUserNickName());
                chatMessage.setSendTime(System.currentTimeMillis());
                chatMessage.setContactType(UserContactTypeEnum.USER.getType());
                chatMessage.setContactId(userContactApply.getApplyUserId());
                chatMessageMapper.insertMessage(chatMessage);

                //发送人都是申请人
                //userId 是同意人
                MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
                //发送给申请的人
                //contactId 是 applyUserId
                String receiveAvatar = this.avatarService.getAvatar(userId);
                messageSendDto.setAvatarUrl(receiveAvatar);
                messageSendDto.setExtendData(receiveUserInfo);
                messageSendDto.setSendUserNickName(applyUserInfo.getNickName());
                messageHandler.sendMessage(messageSendDto);



                //发送给同意人
                messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
                messageSendDto.setSendUserId(userContactApply.getApplyUserId());
                messageSendDto.setContactName(userContactApply.getApplyUserNickName());
                messageSendDto.setContactId(userId);
                messageSendDto.setSendUserNickName(applyUserInfo.getNickName());
                messageSendDto.setAvatarUrl(this.avatarService.getAvatar(userContactApply.getContactId()));
                String applyAvatar = this.avatarService.getAvatar(userContactApply.getApplyUserId());
                messageSendDto.setAvatarUrl(applyAvatar);
                messageHandler.sendMessage(messageSendDto);
            } else {
                //加入群组
                ChatSessionUser chatSessionUser = new ChatSessionUser();
                chatSessionUser.setUserId(userContactApply.getApplyUserId());
                chatSessionUser.setContactId(userContactApply.getContactId());
                GroupInfo groupInfo = this.groupInfoMapper.selectGroupById(userContactApply.getContactId());
                System.out.println(groupInfo);
                chatSessionUser.setContactName(groupInfo.getGroupName());
                chatSessionUser.setSessionId(sessionId);
                this.chatSessionUserMapper.insertChatSessionUser(chatSessionUser);


                redisComponent.addUserContact(userContactApply.getApplyUserId(), groupInfo.getGroupId());
                channelContextUtils.addUser2Group(userContactApply.getApplyUserId(), groupInfo.getGroupId());

                UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(userContactApply.getApplyUserId());
                String sendMessage = String.format(MessageTypeEnum.ADD_GROUP.getInitMessage(), applyUserInfo.getNickName());

                //增加session信息
                ChatSession chatSession = new ChatSession();
                chatSession.setSessionId(sessionId);
                chatSession.setLastReceiveTime(System.currentTimeMillis());
                chatSession.setLastMessage(sendMessage);
                this.chatSessionMapper.updateSession(chatSession);

                //增加聊天消息
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSessionId(sessionId);
                chatMessage.setMessageType(MessageTypeEnum.ADD_GROUP.getType());
                chatMessage.setMessageContent(sendMessage);
                chatMessage.setSendTime(System.currentTimeMillis());
                chatMessage.setContactId(groupInfo.getGroupId());
                chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
                chatMessage.setStatus(1);
                this.chatMessageMapper.insertMessage(chatMessage);


                MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
                messageSendDto.setContactId(userContactApply.getContactId());

                //获取群总人数
                List<UserContact> userContactList = this.userContactMapper.selectByContactId(userContactApply.getContactId());
                Integer memberCount = userContactList.size();
                messageSendDto.setMemberCount(memberCount);
                messageSendDto.setContactName(groupInfo.getGroupName());
                String avatarUrl = this.avatarService.getAvatar(userContactApply.getContactId());
                messageSendDto.setAvatarUrl(avatarUrl);
                messageHandler.sendMessage(messageSendDto);
            }

        }
//        this.userContactApplyMapper.deleteByApplyId(Integer.valueOf(applyId));
    }

    @Override
    public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) {
        UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(tokenUserInfoDto.getUserId());
        UserContactTypeEnum userContactTypeEnum;
        if (contactId.startsWith("G")) {
            userContactTypeEnum = UserContactTypeEnum.GROUP;
        } else {
            userContactTypeEnum = UserContactTypeEnum.USER;
        }
        System.out.println(userContactTypeEnum);

        //申请人id
        String applyUserId = tokenUserInfoDto.getUserId();

        //默认申请信息
        if(StringTools.isEmpty(applyInfo)) {
            applyInfo = String.format(Constants.APPLY_INFO, tokenUserInfoDto.getNickName());
        }
        if (contactId.startsWith("U")) {
            applyInfo = "[好友申请]: " + applyInfo;
        } else {
            GroupInfo groupInfo = this.groupInfoMapper.selectGroupById(contactId);
            applyInfo = "[入群申请:申请加入" + groupInfo.getGroupName() + "群聊]: "  + applyInfo;
        }

        Long curTime = System.currentTimeMillis();

        Integer joinType = null;
        String receiveUserId = contactId;

        if (UserContactTypeEnum.GROUP == userContactTypeEnum) {
            GroupInfo groupInfo = groupInfoMapper.selectGroupById(contactId);
            if (groupInfo == null || groupInfo.getStatus() == 0) {
                throw new BusinessException("该群不存在或已封禁！");
            }
            receiveUserId = groupInfo.getGroupOwnerName();
        } else {
            UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
            if (userInfo == null || userInfo.getStatus() == 0) {
                throw new BusinessException("该用户不存在或已被封禁！");
            }
        }

        UserContactApply dbContactApply = this.userContactApplyMapper.selectByApplyUserAndReceiver(applyUserId, contactId);
        if (dbContactApply == null) {
            UserContactApply userContactApply = new UserContactApply();
            userContactApply.setApplyUserId(applyUserId);
            userContactApply.setApplyUserNickName(applyUserInfo.getNickName());
            userContactApply.setReceiveUserId(receiveUserId);
            userContactApply.setApplyInfo(applyInfo);
            Date date = new Date();
            userContactApply.setLastApplyTime(date);
            userContactApply.setContactId(contactId);
            if (contactId.startsWith("G")) userContactApply.setContactType(1);
            else {
                userContactApply.setContactType(0);
                UserInfo receiveUserInfo = this.userInfoMapper.selectByUserId(contactId);
                userContactApply.setReceiveUserNickName(receiveUserInfo.getNickName());
            }
            this.userContactApplyMapper.insertUserContactApply(userContactApply);
        } else {
            //已经存在，更新状态
            dbContactApply.setApplyInfo(applyInfo);
            dbContactApply.setLastApplyTime(new Date());
            this.userContactApplyMapper.updateUserContactApply(dbContactApply);
        }


        if (dbContactApply == null) {
            MessageSendDto messageSendDto = new MessageSendDto();
            messageSendDto.setSendUserId(applyUserId);
            messageSendDto.setMessageType(MessageTypeEnum.CONTACT_APPLY.getType());
            messageSendDto.setMessageContent(applyInfo);
            messageSendDto.setContactId(receiveUserId);
            messageHandler.sendMessage(messageSendDto);
        }

        return 1;
    }

}
