package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.entity.LogLogin;
import com.warehouse.sys.service.ILogLoginService;
import com.warehouse.sys.vo.LogLoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-28
 */
@RestController
@RequestMapping("/log-login")
public class LogLoginController {

    @Autowired
    private ILogLoginService logLoginService;

    /**
     * @description: 查询登录信息
     * @param: logLoginVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/2/28 18:08
     */
    @RequestMapping("/loadAll")
    public DataGridView loadAllLog(LogLoginVo logLoginVo) {

        IPage<LogLogin> logLoginPage = new Page<>(logLoginVo.getPage(), logLoginVo.getLimit());
        QueryWrapper<LogLogin> wrapper = new QueryWrapper<>();

        // 添加查询条件，按照登录名查询
        wrapper.like(StringUtils.isNotBlank(logLoginVo.getLoginname()),
                "loginname", logLoginVo.getLoginname());
        // 按照登录IP查询
        wrapper.like(StringUtils.isNotBlank(logLoginVo.getLoginip()),
                "loginip", logLoginVo.getLoginip());
        // 按照登录时间范围查询
        wrapper.ge(logLoginVo.getStartTime() != null,
                "logintime", logLoginVo.getStartTime());
        wrapper.le(logLoginVo.getEndTime() != null,
                "logintime", logLoginVo.getEndTime());
        // 按照登录时间排序，新登陆的在前面
        wrapper.orderByDesc("logintime");

        logLoginService.page(logLoginPage, wrapper);

        return new DataGridView(logLoginPage.getTotal(), logLoginPage.getRecords());
    }

    /**
     * @description: 按照id删除登陆日志信息
     * @param: id
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/2/29 13:26
     */
    @RequestMapping("/delete")
    public ResultObj deleteLogLogin(Integer id) {
        try {
            boolean removeSuccess = logLoginService.removeById(id);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * @description: 批量删除
     * @param: loginVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/2/29 13:44
     */
    @RequestMapping("/batchDelete")
    public ResultObj batchDeleteLog(LogLoginVo loginVo) {
        try {
            ArrayList<Integer> logIds = new ArrayList<>();
            Collections.addAll(logIds, loginVo.getIds());
            boolean removeSuccess = logLoginService.removeByIds(logIds);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}

