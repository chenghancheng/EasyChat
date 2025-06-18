package com.example.easychat.controller;


import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.Avatar;
import com.example.easychat.entity.po.GroupInfo;
import com.example.easychat.entity.po.UserContact;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.mapper.UserContactMapper;
import com.example.easychat.service.AvatarService;
import com.example.easychat.service.GroupInfoService;
import com.example.easychat.service.UserContactService;
import com.example.easychat.service.UserInfoService;
import com.example.easychat.service.impl.GroupInfoServiceImpl;
import com.example.easychat.service.impl.UserContactServiceImpl;
import com.example.easychat.utils.StringTools;
import org.apache.el.parser.Token;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupInfoController extends ABaseController {

    @Resource
    private GroupInfoService groupInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserContactService userContactService;

    @Resource
    private AvatarService avatarService;

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/saveGroup")
    public void handleOptions() {
        // Spring 会通过 CORS 配置处理 OPTIONS 请求
    }

    @PostMapping("/saveGroup")
    public ResponseVO saveGroup(@RequestBody Map<String, String> saveGroupRequest, HttpServletRequest request) {
        System.out.println("正在创建群聊");
        System.out.println(saveGroupRequest);


//        String token = request.getHeader("token");
//        System.out.println(saveGroupRequest);
//        System.out.println(request.getHeader("token"));
//        System.out.println(request.getHeader("Authorization"));
        System.out.println("123456");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        System.out.println(tokenUserInfoDto);
        System.out.println("123456");
        String groupName = saveGroupRequest.get("groupName");
        String groupOwnerName = tokenUserInfoDto.getUserId();
        String groupNotice = saveGroupRequest.get("groupNotice");
        String groupId = saveGroupRequest.get("groupId");
//        MultipartFile avatarFile = (MultipartFile) saveGroupRequest.get("avatarFile"); // 封面大图
//        MultipartFile avatarCover = (MultipartFile) saveGroupRequest.get("avatarCover"); // 封面小图

        System.out.println("hello");

        GroupInfo dbGroupInfo = this.groupInfoService.getGroupById(groupId);
        System.out.println(dbGroupInfo);
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupId(groupId);
        System.out.println(groupId);
        groupInfo.setGroupName(groupName);
        System.out.println(groupName);
        groupInfo.setGroupNotice(groupNotice);
        groupInfo.setGroupOwnerName(groupOwnerName);
        groupInfo.setStatus((byte) 1);
        if (dbGroupInfo != null) {
            groupInfo.setCreateTime(dbGroupInfo.getCreateTime());
        }



        System.out.println(groupName);
        this.groupInfoService.saveGroup(groupInfo);

        Avatar avatar = new Avatar();
        avatar.setId(groupInfo.getGroupId());
        String avatarUrl = null;
        avatarUrl = this.avatarService.getAvatar(groupInfo.getGroupId());
        if (StringTools.isEmpty(avatarUrl)) {
            avatarUrl = "http://10.29.61.159:5050/images/group.jpg";
            avatar.setId(groupInfo.getGroupId());
            avatar.setAvatarUrl(avatarUrl);
            this.avatarService.insertAvatar(avatar);
        }
        groupInfo.setAvatarUrl(avatarUrl);
        return getSuccessResponseVO(groupInfo);
    }

    @PostMapping("/exit/{groupId}")
    public ResponseVO exitGroup(@PathVariable("groupId") Long groupId, @RequestBody TokenUserInfoDto user) {
        try {
            // 将 user.getUserId() 转换为 String 类型
            String userId = user.getUserId();
            // 将 groupId 转换为 String 类型
            String groupIdStr = String.valueOf(groupId);
    
            // 调用服务层退出群聊，传递 String 类型的参数
            groupInfoService.exitGroup(groupIdStr, userId);
            return ResponseVO.success("退出群聊成功");
        } catch (BusinessException e) {
            return ResponseVO.error("退出群聊失败", e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseVO.error("用户ID格式错误", "无法将用户ID转换为Long类型");
        }
    }
    
    

    @RequestMapping("/loadMyGroup")
    public ResponseVO loadGroup(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        String userId = tokenUserInfoDto.getUserId();
        List<GroupInfo> groupInfos = groupInfoService.searchGroupsByName(userId);
        return getSuccessResponseVO(groupInfos);
    }


    @RequestMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(HttpServletRequest request,@RequestBody Map<String, String> getGroupInfoMap) {
        GroupInfo groupInfo = getGroupDetailCommon(request, getGroupInfoMap);
        String groupId = groupInfo.getGroupId();
        List<UserContact> userContactList = this.userContactService.getContactsByContactId(groupId);
        List<UserInfo> userList = new ArrayList<>();
        for (UserContact userContact : userContactList) {
            String userId = userContact.getUserId();
            UserInfo userInfo = this.userInfoService.getUserByUserId(userId);
            userInfo.setAvatarUrl(this.avatarService.getAvatar(userId));
            userList.add(userInfo);
        }
        groupInfo.setUserList(userList);
        return getSuccessResponseVO(groupInfo);
    }

    private GroupInfo getGroupDetailCommon(HttpServletRequest request,@RequestBody Map<String, String> getGroupInfoMap) {
        String groupId = getGroupInfoMap.get("groupId");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);

        UserContact userContact = this.userContactService.getContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), groupId);
        if (null == userContact || userContact.getContactType() != 1) {
            throw new BusinessException("不在该群组中！");
        }
        GroupInfo groupInfo = this.groupInfoService.getGroupById(groupId);
        if (groupInfo == null || groupInfo.getStatus() == 0) {
            throw new BusinessException("该群不存在或已被封禁！");
        }
        return groupInfo;
    }

    @RequestMapping("/getGroupInfo4Chat")
    public ResponseVO getGroupInfo4Chat(HttpServletRequest request,@RequestBody Map<String, String> getGroupInfoMap) {
        GroupInfo groupInfo = getGroupDetailCommon(request, getGroupInfoMap);

        return getSuccessResponseVO(groupInfo);
    }

}
