package com.example.easychat.service;

import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.UserContactApply;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface UserContactApplyService {

    /**
     * 根据申请ID获取申请信息
     * @param applyId 申请ID
     * @return UserContactApply对象
     */
    UserContactApply getApplyById(Integer applyId);

    /**
     * 获取某用户收到的所有申请
     * @param receiveUserId 接收人ID
     * @return 申请列表
     */
    List<UserContactApply> getAppliesByReceiveUserId(String receiveUserId);

    /**
     * 获取某用户发出的所有申请
     * @param applyUserId 申请人ID
     * @return 申请列表
     */
    List<UserContactApply> getAppliesByApplyUserId(String applyUserId);

    /**
     * 根据申请人和联系人查询好友申请
     * @param applyUserId 申请人ID
     * @param receiveUserId 接收人ID
     * @return 申请列表
     */
     UserContactApply getAppliesByApplyUserAndReceiver(String applyUserId, String receiveUserId);

    /**
     * 创建新的联系人申请
     * @param applyUserId 申请人ID
     * @param receiveUserId 接收人ID
     * @param contactType 联系人类型
     * @param contactId 联系人群组ID（可为空）
     * @param applyInfo 申请信息
     */
    void createApply(String applyUserId, String receiveUserId, Integer contactType, String contactId, String applyInfo);

    /**
     * 更新申请状态
     * @param applyId 申请ID
     * @param status 新状态
     */
    void updateApplyStatus(Integer applyId, Integer status);

    /**
     * 获取用户的最新申请
     * @param userId 用户ID
     * @param limit 获取的申请数目
     * @return 最新的申请列表
     */
    List<UserContactApply> getRecentAppliesByUserId(String userId, Integer limit);


    List<UserContactApply> loadApply(String userId);

    List<UserContactApply> loadReceiveApply(String userId);

    Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo);


    void dealWithApply(String userId, String applyId, String ok);


}
