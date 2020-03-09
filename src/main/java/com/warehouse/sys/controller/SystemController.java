package com.warehouse.sys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: warehouse
 * @description: 系统控制层
 * @author: Dawn
 * @create: 2020-02-24 19:32
 */
@Controller
@RequestMapping("/sys")
public class SystemController {

    /**
     * 跳转到登录页面
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "system/login/login";
    }

    /**
     * 跳转到首页
     */
    @RequestMapping("/index")
    public String index() {
        return "system/index/index";
    }

    /**
     * 跳转到工作台
     */
    @RequestMapping("/toDesktop")
    public String toDesktop() {
        return "system/main/main";
    }

    /**
     * 跳转到登录日志管理
     */
    @RequestMapping("/toLogManager")
    public String toLogManager() {
        return "system/logLogin/logManager";
    }

    /**
     * 跳转到公告管理
     */
    @RequestMapping("/toNoticeManager")
    public String toNoticeManager() {
        return "system/notice/noticeManager";
    }

    /**
     * 跳转到部门管理
     */
    @RequestMapping("/toDeptManager")
    public String toDeptManager() {
        return "system/dept/deptManager";
    }

    /**
     * 跳转到部门管理-left
     */
    @RequestMapping("/toDeptLeft")
    public String toDeptLeftManager() {
        return "system/dept/deptLeft";
    }

    /**
     * 跳转到部门管理-right
     */
    @RequestMapping("/toDeptRight")
    public String toDeptRightManager() {
        return "system/dept/deptRight";
    }

    /**
     * 跳转到菜单管理
     */
    @RequestMapping("/toMenuManager")
    public String toMenuManager() {
        return "system/menu/menuManager";
    }

    /**
     * 跳转到菜单管理-left
     */
    @RequestMapping("/toMenuLeft")
    public String toMenuLeftManager() {
        return "system/menu/menuLeft";
    }

    /**
     * 跳转到菜单管理-right
     */
    @RequestMapping("/toMenuRight")
    public String toMenuRightManager() {
        return "system/menu/menuRight";
    }

    /**
     * 跳转到权限管理
     */
    @RequestMapping("/toPermissionManager")
    public String toPermissionManager() {
        return "system/permission/permissionManager";
    }

    /**
     * 跳转到权限管理-left
     */
    @RequestMapping("/toPermissionLeft")
    public String toPermissionLeftManager() {
        return "system/permission/permissionLeft";
    }

    /**
     * 跳转到权限管理-right
     */
    @RequestMapping("/toPermissionRight")
    public String toPermissionRightManager() {
        return "system/permission/permissionRight";
    }

    /**
     * 跳转到角色管理
     */
    @RequestMapping("/toRoleManager")
    public String toRoleManager() {
        return "system/role/roleManager";
    }

    /**
     * 跳转到用户管理
     */
    @RequestMapping("/toUserManager")
    public String toUserManager() {
        return "system/user/userManager";
    }

}