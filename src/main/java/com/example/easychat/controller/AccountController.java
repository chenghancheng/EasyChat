package com.example.easychat.controller;

import com.example.easychat.entity.constants.Constants;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.po.Avatar;
import com.example.easychat.entity.po.UserInfo;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.entity.vo.UserInfoVO;
import com.example.easychat.exception.BusinessException;
import com.example.easychat.redis.RedisUtils;
import com.example.easychat.service.AvatarService;
import com.example.easychat.service.UserInfoService;
import com.example.easychat.utils.CopyTools;
import com.example.easychat.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.catalina.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private AvatarService avatarService;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello!";
    }

    @RequestMapping("/getUser")
    public ResponseVO getUser(@RequestBody Map<String, String> getUserMap) {
        String userId = getUserMap.get("userId");
        UserInfo userInfo = new UserInfo();
        userInfo = this.userInfoService.getUserByUserId(userId);
        String avatarUrl = this.avatarService.getAvatar(userId);
        userInfo.setAvatarUrl(avatarUrl);
        return getSuccessResponseVO(userInfo);
    }


    @RequestMapping("/checkcode")
    public ResponseVO checkcode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text(); //该验证码的答案
        String checkCodeKey = UUID.randomUUID().toString(); //该验证码的唯一标识
        String checkCodeBase64 = captcha.toBase64(); //将这个验证码改为Base64编码

        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, Constants.REDIS_TIME_1MIN * 10);

        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        System.out.println(checkCodeKey);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/register")
    public ResponseVO register(@RequestBody Map<String, String> registerRequest) {
        try {
            // 从 Map 中获取参数
            String checkCodeKey = registerRequest.get("checkCodeKey");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");
            String nickname = registerRequest.get("nickname");
            String checkCode = registerRequest.get("checkCode");

            // 检查验证码是否合法
            if (checkCode == null || checkCodeKey == null) {
                throw new BusinessException("验证码或验证码键不能为空");
            }

            // 从Redis中获取存储的验证码
            String redisCheckCode = (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);

            // 如果验证码无效或已过期
            if (redisCheckCode == null) {
                throw new BusinessException("验证码已过期或无效");
            }

            // 验证输入的验证码是否与Redis中的验证码一致
            if (!checkCode.equalsIgnoreCase(redisCheckCode)) {
                throw new BusinessException("图片验证码不正确");
            }

            // 注册用户
            userInfoService.register(email, nickname, password);

            UserInfo userInfo = this.userInfoService.getUserByEmail(email);
            String userId = userInfo.getUserId();
            String avatarUrl = "http://10.29.61.159:5050/images/moren.png";
            Avatar avatar = new Avatar();
            avatar.setId(userId);
            avatar.setAvatarUrl(avatarUrl);
            this.avatarService.insertAvatar(avatar);
            // 返回成功响应
            return getSuccessResponseVO(userInfo);
        } finally {
            // 删除Redis中的验证码
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + registerRequest.get("checkCodeKey"));
        }
}


    @RequestMapping("/login")
    public ResponseVO login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        if (email == null || password == null) {
            throw new BusinessException("邮箱或密码不能为空！");
        }
        UserInfoVO userInfoVO = userInfoService.login(email, password);

        return getSuccessResponseVO(userInfoVO);
    }

    @RequestMapping("/update")
    public ResponseVO update(HttpServletRequest request,@RequestBody Map<String, String> updateMap) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setUserId(tokenUserInfoDto.getUserId());
        String updateNickName = updateMap.get("userNickName");
        String updatePersonalSignature = updateMap.get("personalSignature");
        String sex = updateMap.get("sex");
        updateUserInfo.setNickName(updateNickName);
        if (sex.equals("1")) {
            updateUserInfo.setSex(1);
        } else {
            updateUserInfo.setSex(0);
        }
        updateUserInfo.setPersonalSignature(updatePersonalSignature);

        System.out.println(updateUserInfo);
        userInfoService.updateUserInfo(updateUserInfo);
        UserInfo userInfo = userInfoService.getUserByUserId(tokenUserInfoDto.getUserId());

        System.out.println("             ");
        System.out.println(userInfo);

        return getSuccessResponseVO(userInfo);
    }

    @RequestMapping("/updatePassword")
    public ResponseVO updatePassword(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String password = map.get("password");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo(request);
        UserInfo userInfo = this.userInfoService.getUserByUserId(tokenUserInfoDto.getUserId());
        userInfo.setPassword(password);
        this.userInfoService.updateUserInfo(userInfo);
        return getSuccessResponseVO(userInfo);
    }


//    @RequestMapping("/getSysSetting")
//    public ResponseVO getSysSetting() {
////        String email = loginRequest.get("email");
////        String password = loginRequest.get("password");
////        if (email == null || password == null) {
////            throw new BusinessException("邮箱或密码不能为空！");
////        }
////        UserInfoVO userInfoVO = userInfoService.login(email, password);
//
//        return getSuccessResponseVO(userInfoVO);
//    }
}
