package com.example.easychat.controller;


import com.example.easychat.entity.vo.ResponseVO;
import com.example.easychat.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;

import static com.example.easychat.controller.ABaseController.STATUC_ERROR;

@RestControllerAdvice
public class AGlobalExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO> handleException(Exception e, HttpServletRequest request) {
        ResponseVO ajaxResponse = new ResponseVO();

        if (e instanceof BusinessException) {
            // 业务错误
            BusinessException businessException = (BusinessException) e;
            ajaxResponse.setCode(businessException.getCode());
            ajaxResponse.setInfo(businessException.getMessage());
            ajaxResponse.setStatus("ERROR");

            return new ResponseEntity<>(ajaxResponse, HttpStatus.BAD_REQUEST); // 400 错误码
        }

        // 处理其他类型的异常
        ajaxResponse.setCode(500);
        ajaxResponse.setInfo("服务器内部错误");
        ajaxResponse.setStatus("ERROR");

        return new ResponseEntity<>(ajaxResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 错误码
    }
}
