package com.example.easychat.service.impl;

import com.example.easychat.config.Appconfig;
import com.example.easychat.entity.constants.Constants;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.ChatSession;
import com.example.easychat.entity.po.ChatSessionUser;
import com.example.easychat.entity.po.UserContact;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.UserInfoVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.ChatSessionMapper;
import com.example.easychat.mapper.ChatSessionUserMapper;
import com.example.easychat.mapper.UserContactMapper;
import com.example.easychat.redis.RedisComponent;
import com.example.easychat.service.UserContactService;
import com.example.easychat.service.UserInfoService;
import com.example.easychat.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final com.example.easychat.mapper.UserInfoMapper userInfoMapper;

    @Autowired
    public UserInfoServiceImpl(com.example.easychat.mapper.UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Resource
    UserContactMapper userContactMapper;

    @Resource
    private Appconfig appconfig;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserContactService userContactService;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Override
    public UserInfo getUserByUserId(String userId) {
        return userInfoMapper.selectByUserId(userId);
    }

    @Override
    public UserInfo getUserByEmail(String email) {
        return userInfoMapper.selectByEmail(email);
    }

    @Override
    public List<UserInfo> getAllUsers() {
        return userInfoMapper.selectAllUsers();
    }

    @Override
    public List<UserInfo> getUsersByStatus(Integer status) {
        return userInfoMapper.selectByStatus(status);
    }

    @Override
    public List<UserInfo> searchUsersByNickName(String nickName) {
        return userInfoMapper.selectByNickName(nickName);
    }

    @Override
    public List<UserInfo> getRecentUsers(Integer limit) {
        return userInfoMapper.selectRecentUsers(limit);
    }

    @Override
    public List<UserInfo> getUsersByAreaName(String areaName) {
        return userInfoMapper.selectByAreaName(areaName);
    }

    @Override
    public List<UserInfo> getUsersBySexAndStatus(Integer sex, Integer status) {
        return userInfoMapper.selectBySexAndStatus(sex, status);
    }

    @Override
    public Integer countTotalUsers() {
        return userInfoMapper.countAllUsers();
    }

    @Override
    public Integer countUsersByStatus(Integer status) {
        return userInfoMapper.countByStatus(status);
    }
    @Override
    public void register(String email, String nickname, String password) {
        // 检查邮箱是否已存在
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);

        if (userInfo != null) {
            throw new BusinessException("邮箱已经存在");
        }

        String userId = StringTools.getUserId();  // 生成用户ID

        String personal_signatrue = "这个人很懒，什么都没有留下。";
        Date curDate = new Date();
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setEmail(email);
        userInfo.setNickName(nickname);
        userInfo.setPassword(StringTools.encodeMD5(password));  // 密码加密
        userInfo.setCreateTime(curDate);
        userInfo.setStatus(1);  // 默认状态为1（正常）
        userInfo.setPersonalSignature(personal_signatrue);

        userInfoMapper.insertUserInfo(userInfo);
        //创建机器人好友
        userContactService.addUserContact4Robot(userId);

    }


    @Override
    public UserInfoVO login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);

        System.out.println(userInfo);

        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或密码错误", 1001);
        }
        if (userInfo.getStatus() == 0) {
            throw new BusinessException("账号已被封禁！", 1002);
        }
        //查询群组、联系人
        System.out.println(userInfo.getUserId());
        System.out.println(userInfo.getNickName());
        String userId = userInfo.getUserId();
        List<UserContact> userFriendsContactList = this.userContactMapper.selectByUserId(userInfo.getUserId());
        List<UserInfo> userFriendsList = new ArrayList<>();
        userFriendsContactList.forEach(userContact -> {
            UserInfo userInfo1 = userInfoMapper.selectByUserId(userContact.getContactId());
            if (userInfo1 != null) {
                userFriendsList.add(userInfo1);
            }
        });
        List<String> contactIdList = userFriendsContactList.stream().map(item->item.getContactId()).collect(Collectors.toList());
        redisComponent.cleanUserContact(userId);
        if (!contactIdList.isEmpty()) {
            redisComponent.addUserContactBatch(userId, contactIdList);
        }



        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(userInfo);
        Long lastHeartBeat = redisComponent.getUserHeartBeat(userInfo.getUserId());
        if (lastHeartBeat != null) {
            throw new BusinessException("账号已在别处登录！");
        }

        String token = StringTools.encodeMD5(tokenUserInfoDto.getUserId()+StringTools.getRandomString(Constants.LENGTH_20));
        tokenUserInfoDto.setToken(token);

        //保存登录信息到redis中
        //userid -> token
        //token -> token封装类
        redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);

//        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);

        //查询会话
        List<ChatSessionUser> chatSessionUserList = this.chatSessionUserMapper.selectByUserId(userId);
        List<String> chatSessionIdList = new ArrayList<>();
        for (ChatSessionUser chatSessionUser : chatSessionUserList) {
            chatSessionIdList.add(chatSessionUser.getSessionId());
        }
        List<ChatSession> sessionList = new ArrayList<>();
        for (String sessionId : chatSessionIdList) {
            ChatSession chatSession = new ChatSession();
            chatSession = this.chatSessionMapper.selectBySessionId(sessionId);
            sessionList.add(chatSession);
        }



        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(userInfo.getUserId());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setToken(tokenUserInfoDto.getToken());
        userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());
        userInfoVO.setContactList(userFriendsList);
        userInfoVO.setSessionList(sessionList);

        System.out.println(userInfoVO.getUserId());
        System.out.println(userInfoVO.getNickName());
        return userInfoVO;
    }

    private TokenUserInfoDto getTokenUserInfoDto(UserInfo userInfo) {
        TokenUserInfoDto tokenUserInfoDto = new TokenUserInfoDto();
        tokenUserInfoDto.setUserId(userInfo.getUserId());
        tokenUserInfoDto.setNickName(userInfo.getNickName());

        String adminEmails = appconfig.getAdminEmails();
        String[] emailArray = adminEmails.split(",");
        if (!adminEmails.isEmpty() && Arrays.asList(emailArray).contains(userInfo.getEmail())) {
            tokenUserInfoDto.setAdmin(true);
        } else {
            tokenUserInfoDto.setAdmin(false);
        }
        return tokenUserInfoDto;
    }


    @Override
    public void updateUserInfo(UserInfo userInfo) {
        UserInfo dbUserInfo = this.userInfoMapper.selectByUserId(userInfo.getUserId());
        if (StringTools.isEmpty(userInfo.getNickName())) userInfo.setNickName(dbUserInfo.getNickName());
        if (StringTools.isEmpty(userInfo.getPassword())) userInfo.setPassword(dbUserInfo.getPassword());
        if (StringTools.isEmpty(userInfo.getPersonalSignature())) userInfo.setPersonalSignature(dbUserInfo.getPersonalSignature());
        String userId = userInfo.getUserId();
        System.out.println("123456789123456789");
        System.out.println(userInfo);
        this.userInfoMapper.updateUserInfo(userInfo);



    }
}
