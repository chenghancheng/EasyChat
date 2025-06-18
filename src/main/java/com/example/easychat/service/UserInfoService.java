package com.example.easychat.service;

import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.UserInfoVO;
import org.apache.el.parser.Token;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public interface UserInfoService {
    UserInfo getUserByUserId(String userId);

    UserInfo getUserByEmail(String email);

    List<UserInfo> getAllUsers();

    List<UserInfo> getUsersByStatus(Integer status);

    List<UserInfo> searchUsersByNickName(String nickName);

    List<UserInfo> getRecentUsers(Integer limit);

    List<UserInfo> getUsersByAreaName(String areaName);

    List<UserInfo> getUsersBySexAndStatus(Integer sex, Integer status);

    Integer countTotalUsers();

    Integer countUsersByStatus(Integer status);
    void register(String email, String nickname, String password);

    UserInfoVO login(String email, String password);

    void updateUserInfo(UserInfo userInfo);
}
