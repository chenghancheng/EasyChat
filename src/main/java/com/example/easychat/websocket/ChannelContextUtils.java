package com.example.easychat.websocket;


import com.example.easychat.entity.dto.MessageSendDto;
import com.example.easychat.entity.dto.WsInitData;
import com.example.easychat.entity.enums.MessageTypeEnum;
import com.example.easychat.entity.enums.UserContactTypeEnum;
import com.example.easychat.entity.po.*;
import com.example.easychat.mapper.*;
import com.example.easychat.redis.RedisComponent;
import com.example.easychat.service.AvatarService;
import com.example.easychat.utils.JsonUtils;
import com.example.easychat.utils.StringTools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelContextUtils {

    private static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private UserContactMapper userContactMapper;

    @Resource
    private AvatarService avatarService;

    public void addContext(String userId, Channel channel){
        System.out.println("我进来了哈哈哈哈哈哈哈");
        String channelId = channel.id().toString();
        AttributeKey attributeKey = null;
        if (!AttributeKey.exists(channelId)) {
            attributeKey = AttributeKey.newInstance(channelId);
        } else {
            attributeKey = AttributeKey.valueOf(channelId);
        }
        channel.attr(attributeKey).set(userId);
        List<String> contactIdList = redisComponent.getUserContactList(userId);
        for (String groupId : contactIdList) {
            if (groupId.startsWith("G")) {
                add2Group(groupId, channel);
            }
        }
        USER_CONTEXT_MAP.put(userId, channel);

        redisComponent.saveHeartBeat(userId);

        //更新用户最后连接时间
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setUserId(userId);
        newUserInfo.setLastLoginTime(new Date());
        this.userInfoMapper.updateUserInfo(newUserInfo);
//        userInfoMapper.updateUserInfoById(userId,null,null,null,null,new Date());

        //给用户发送消息
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);

        //1.查询会话信息,查询所有的会话

        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectByUserId(userId);
        System.out.println(chatSessionUserList);
        for (ChatSessionUser chatSessionUser : chatSessionUserList) {
            ChatSession chatSession = this.chatSessionMapper.selectBySessionId(chatSessionUser.getSessionId());
            chatSessionUser.setLastMessage(chatSession.getLastMessage());
            chatSessionUser.setLastReceiveTime(chatSession.getLastReceiveTime());
            String avatarUrl = this.avatarService.getAvatar(chatSessionUser.getContactId());
            chatSessionUser.setAvatarUrl(avatarUrl);
        }

        WsInitData wsInitData = new WsInitData();
        wsInitData.setChatSessionList(chatSessionUserList);


        //2.查询聊天消息
        Long lastOffTime = userInfo.getLastOffTime();
        List<ChatMessage> chatMessageList = this.chatMessageMapper.selectOffMessages(userId, lastOffTime);

        List<UserContact> userContactList = this.userContactMapper.selectByUserId(userId);
        for (UserContact userContact : userContactList) {
            if (userContact.getContactId().startsWith("G")) {
                String groupId = userContact.getContactId();
                List<ChatMessage> chatMessageList2 = this.chatMessageMapper.selectOffMessages(groupId, lastOffTime);
                for (ChatMessage chatMessage : chatMessageList2) {
                    chatMessageList.add(chatMessage);
                }
            }
        }

        wsInitData.setChatMessageList(chatMessageList);



        //发送消息
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitData);

        System.out.println("准备发送消息。。。。。。。。。。。。。。。。。。。。。。");
        sendMsg(messageSendDto, userId);
    }

    public void sendMsg(MessageSendDto messageSendDto, String receiveId){
        if (receiveId == null) {
            return;
        }
        Channel sendChannel = USER_CONTEXT_MAP.get(receiveId);
        if (sendChannel == null) {
            return;
        }
        if (MessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())) {
            UserInfo userInfo = (UserInfo) messageSendDto.getExtendData();
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
            messageSendDto.setContactId(userInfo.getUserId());
            messageSendDto.setContactName(userInfo.getNickName());
            messageSendDto.setExtendData(null);
        } else {
            messageSendDto.setContactId(messageSendDto.getSendUserId());
            messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        }
        String sendUserId = messageSendDto.getSendUserId();
        if (!StringTools.isEmpty(sendUserId)) {
            System.out.println("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
            UserInfo userInfo = this.userInfoMapper.selectByUserId(messageSendDto.getSendUserId());
            System.out.println(userInfo);
            messageSendDto.setAvatarUrl(avatarService.getAvatar(userInfo.getUserId()));
            System.out.println("发送消息"+ messageSendDto);
        }
        System.out.println(messageSendDto);
        sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
        //        sendChannel.writeAndFlush(new TextWebSocketFrame("hello"));
    }

    public void addUser2Group(String userId, String groupId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        add2Group(groupId, channel);
    }

    private void add2Group(String groupId, Channel channel) {
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        if (channel == null) {
            return;
        }
        group.add(channel);
    }

    public void removeContext(Channel channel) {
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if (userId == null || userId.isEmpty()) {
            return;
//            USER_CONTEXT_MAP.remove(userId);
        }
        redisComponent.removeUserHeartBeat(userId);
        //更新用户最后离线时间
        UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        userInfo.setLastOffTime(System.currentTimeMillis());
        System.out.println(userInfo);
        userInfoMapper.updateUserInfo(userInfo);
    }

    public void sendMessage(MessageSendDto messageSendDto) {
        Long tot = this.chatMessageMapper.selectByAllMessage();
        messageSendDto.setMessageId(tot);
        UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(messageSendDto.getContactId());
        String contactId = messageSendDto.getContactId();
        System.out.println(messageSendDto);
        if (contactId.startsWith("U")) {
            send2User(messageSendDto);
        } else {
            send2Group(messageSendDto);
        }
//        switch (userContactTypeEnum) {
//            case USER:
//
//                break;
//            case GROUP:
//
//                break;
//        }
    }

    //发送给用户
    private void send2User(MessageSendDto messageSendDto) {
        String contactId = messageSendDto.getContactId();
        if (StringTools.isEmpty(contactId)) return;
        sendMsg(messageSendDto, contactId);
    }

    //发送给群组
    private void send2Group(MessageSendDto messageSendDto) {
        String sendUserId = messageSendDto.getSendUserId();
        if (!StringTools.isEmpty(sendUserId)) {
            UserInfo userInfo = this.userInfoMapper.selectByUserId(messageSendDto.getSendUserId());
            messageSendDto.setAvatarUrl(this.avatarService.getAvatar(userInfo.getUserId()));
        }
        System.out.println("这是群组消息。。。");
        if (messageSendDto.getContactId() == null || messageSendDto.getContactId().isEmpty()) {
            return;
        }
        ChannelGroup channelGroup = GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (channelGroup == null || channelGroup.isEmpty()) return;

        channelGroup.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));

    }


    public void closeContext(String userId) {
        if (userId == null || userId.isEmpty()) return;
        Channel channel = USER_CONTEXT_MAP.get(userId);
        redisComponent.cleanUserTokenByUserId(userId);
        if (channel == null) return;
        channel.close();
    }

}
