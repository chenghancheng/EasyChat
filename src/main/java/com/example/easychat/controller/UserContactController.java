package com.example.easychat.controller;


import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.dto.UserContactSearchResultDto;
import com.example.easychat.entity.po.GroupInfo;
import com.example.easychat.entity.po.UserContact;
import com.example.easychat.entity.po.UserContactApply;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.service.*;

import gov.nist.core.Token;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact")
public class UserContactController extends ABaseController {

    @Resource
    private UserContactService userContactService;

    @Resource
    private UserInfoService userInfoService;


    @Resource
    private GroupInfoService groupInfoService;

    @Resource
    private UserContactApplyService userContactApplyService;

    @Resource
    private AvatarService avatarService;


    @RequestMapping("/getContact")
    public ResponseVO getContact(HttpServletRequest request) {
        System.out.println(request + "哈哈哈哈哈哈哈哈哈哈");
        System.out.println(request.getHeader("token"));
        System.out.println("......................");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        List<UserContact> userContactList = this.userContactService.getContactsByUserId(tokenUserInfoDto.getUserId());
        for (UserContact userContact : userContactList) {
            if (userContact.getContactId().startsWith("U")) {
                UserInfo userInfo = this.userInfoService.getUserByUserId(userContact.getContactId());
                userContact.setContactName(userInfo.getNickName());
            } else {
                GroupInfo groupInfo = this.groupInfoService.getGroupById(userContact.getContactId());
                userContact.setContactName(groupInfo.getGroupName());
            }
            String url = this.avatarService.getAvatar(userContact.getContactId());
            userContact.setAvatarUrl(url);
        }
        System.out.println(userContactList);
        return getSuccessResponseVO(userContactList);
    }

    @RequestMapping("/getUserContact")
    public ResponseVO getUserContact(HttpServletRequest request) {
        System.out.println("-------------查询好友----------------");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        List<UserContact> userContactList = this.userContactService.getContactsByUserId(tokenUserInfoDto.getUserId());
        List<UserContact> resList = new ArrayList<>();
        for (UserContact userContact : userContactList) {
            if (userContact.getContactId().startsWith("U")) {
                UserInfo userInfo = this.userInfoService.getUserByUserId(userContact.getContactId());
                userContact.setContactName(userInfo.getNickName());
            } else {
                GroupInfo groupInfo = this.groupInfoService.getGroupById(userContact.getContactId());
                userContact.setContactName(groupInfo.getGroupName());
            }
            String url = this.avatarService.getAvatar(userContact.getContactId());
            userContact.setAvatarUrl(url);
            if (userContact.getContactId().startsWith("U")) {
                resList.add(userContact);
            }
        }
        System.out.println("-------------查询好友----------------");
        System.out.println(resList);
        System.out.println("-------------查询好友----------------");
        return getSuccessResponseVO(resList);
    }

    @RequestMapping("/getGroupContact")
    public ResponseVO getGroupContact(HttpServletRequest request) {
        System.out.println("-------------查询群组----------------");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        List<UserContact> userContactList = this.userContactService.getContactsByUserId(tokenUserInfoDto.getUserId());
        List<UserContact> resList = new ArrayList<>();
        for (UserContact userContact : userContactList) {
            if (userContact.getContactId().startsWith("U")) {
                UserInfo userInfo = this.userInfoService.getUserByUserId(userContact.getContactId());
                userContact.setContactName(userInfo.getNickName());
            } else {
                GroupInfo groupInfo = this.groupInfoService.getGroupById(userContact.getContactId());
                userContact.setContactName(groupInfo.getGroupName());
            }
            String url = this.avatarService.getAvatar(userContact.getContactId());
            userContact.setAvatarUrl(url);
            if (userContact.getContactId().startsWith("G")) {
                resList.add(userContact);
            }
        }
        System.out.println("-------------查询群组----------------");
        System.out.println(resList);
        System.out.println("-------------查询群组----------------");
        return getSuccessResponseVO(resList);
    }

    @RequestMapping("/search")
    public ResponseVO search(HttpServletRequest request,@RequestBody Map<String, String> searchMap){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String contactId = searchMap.get("contactId");
        UserContactSearchResultDto userContactSearchResultDto = userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
        return getSuccessResponseVO(userContactSearchResultDto);
    }

    @RequestMapping("/searchApply")
    public ResponseVO searchApply(@RequestBody Map<String, String> searchApplyMap) {
        String userId = searchApplyMap.get("userId");
        List<UserContactApply> userContactApplyList = userContactApplyService.loadReceiveApply(userId);
        for (UserContactApply userContactApply : userContactApplyList) {
            UserInfo applyUserInfo = userInfoService.getUserByUserId(userContactApply.getApplyUserId());
            userContactApply.setApplyUserNickName(applyUserInfo.getNickName());

            UserInfo receiveUserInfo = userInfoService.getUserByUserId(userContactApply.getReceiveUserId());
            userContactApply.setReceiveUserNickName(receiveUserInfo.getNickName());

            userContactApply.setAvatarUrl(avatarService.getAvatar(applyUserInfo.getUserId()));
        }
        return getSuccessResponseVO(userContactApplyList);
    }



    @RequestMapping("/applyAdd")
    public ResponseVO applyAdd(HttpServletRequest request, @RequestBody Map<String, String> applyAddMap) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String contactId = applyAddMap.get("contactId");
        String applyInfo = applyAddMap.get("applyInfo");
        System.out.println(request);
        System.out.println(applyAddMap);


        Integer joinType = userContactApplyService.applyAdd(tokenUserInfoDto, contactId, applyInfo);
        return getSuccessResponseVO(joinType);
    }

    @RequestMapping("/loadApply")
    public ResponseVO loadApply(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String userId = tokenUserInfoDto.getUserId();
        List<UserContactApply> userContactApplyList = userContactApplyService.loadApply(userId);
        return getSuccessResponseVO(userContactApplyList);
    }

    @RequestMapping("/dealWithApply")
    private ResponseVO dealWithApply(HttpServletRequest request, @RequestBody Map<String, String> dealMap) {
        String applyId = dealMap.get("applyId");
        String ok = dealMap.get("ok");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String userId = tokenUserInfoDto.getUserId();
        this.userContactApplyService.dealWithApply(userId, applyId, ok);

        return getSuccessResponseVO(null);
    }


    /**
     * 删除联系人
     * @param userId 用户ID
     * @param contactId 联系人ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
public ResponseVO deleteContact(@RequestParam String userId, @RequestParam String contactId) {
    boolean success = userContactService.deleteContact(userId, contactId);
    if (success) {
        return ResponseVO.success("联系人删除成功");
    } else {
        return ResponseVO.error("删除联系人失败", null);
    }
}


}
