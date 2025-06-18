package com.example.easychat.controller;

import com.example.easychat.entity.constants.Constants;
import com.example.easychat.entity.dto.TokenUserInfoDto;
import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.entity.enums.ResponseCodeEnum;
import com.example.easychat.redis.RedisUtils;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class ABaseController {
    protected static final String STATUC_SUCCESS = "success";
    protected static final String STATUC_ERROR = "error";

    @Resource
    RedisUtils redisUtils;
    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected TokenUserInfoDto getTokenUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto)redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
        return tokenUserInfoDto;
    }

}
