package com.warehouse.sys.controller;

import com.warehouse.sys.common.constants.WebConstants;
import com.warehouse.sys.common.utils.WebUtils;
import com.warehouse.sys.common.entity.ActiveUser;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.entity.LogLogin;
import com.warehouse.sys.service.ILogLoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 * 登录控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-24
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ILogLoginService logLoginService;

    /* 登录测试数据
     * 超级管理员 system 123456
     * */

    /**
     * @description: 登录功能
     * @param: loginname
     * @param: pwd
     * @return: com.warehouse.sys.commmon.entity.ResultObj
     * @author: Dawn
     * @date: 2020/2/28 12:35
     */
    @PostMapping("/login")
    public ResultObj login(String loginname, String pwd) {

        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken usernamePasswordToken =
                new UsernamePasswordToken(loginname, pwd);
        try {
            // shiro登录
            subject.login(usernamePasswordToken);
            // 获取登录的账户
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            // 自建工具类获取session，添加用户信息到session中
            WebUtils.getSession().setAttribute(WebConstants.USER_ATTRIBUTE,
                    activeUser.getUser());

            // 填写登录日志
            // 获取日志登录名
            String logLoginname=activeUser.getUser().getName()+"-"+activeUser.getUser().getLoginname();
            // 获取IP
            String remoteHost = WebUtils.getRequest().getRemoteAddr();
            LogLogin newLogLogin=LogLogin.builder()
                    .loginname(logLoginname)
                    .loginip(remoteHost)
                    .logintime(new Date())
                    .build();
            // 保存登录日志
            logLoginService.save(newLogLogin);

            // 自定义的返回数据格式
            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR;
        }
    }

}

