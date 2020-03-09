package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.constants.WebConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.common.utils.WebUtils;
import com.warehouse.sys.entity.Notice;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.INoticeService;
import com.warehouse.sys.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-29
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    /**
     * @description: 查询公告
     * @param: noticeVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/3/1 14:48
     */
    @RequestMapping("/loadAll")
    public DataGridView loadAllNotice(NoticeVo noticeVo) {

        IPage<Notice> noticeIPage = new Page<>(
                noticeVo.getPage(),noticeVo.getLimit());
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(noticeVo.getTitle()),
                "title", noticeVo.getTitle());
        wrapper.like(StringUtils.isNotBlank(noticeVo.getOpername()),
                "opername", noticeVo.getOpername());
        wrapper.ge(noticeVo.getStartTime() != null,
                "createtime", noticeVo.getStartTime());
        wrapper.le(noticeVo.getEndTime() != null,
                "createtime", noticeVo.getEndTime());
        wrapper.orderByDesc("createtime");

        noticeService.page(noticeIPage, wrapper);

        return new DataGridView(noticeIPage.getTotal(), noticeIPage.getRecords());
    }

    /**
     * @description: 添加公告
     * @param: noticeVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/1 20:49
     */
    @RequestMapping("/add")
    public ResultObj add(NoticeVo noticeVo){
        try {
            // 设置添加时间
            noticeVo.setCreatetime(new Date());
            // 设置发布人
            User user = (User) WebUtils.getSession()
                    .getAttribute(WebConstants.USER_ATTRIBUTE);

            noticeVo.setOpername(user.getName());
            boolean saveSuccess = noticeService.save(noticeVo);
            if (saveSuccess){
                return ResultObj.INSERT_SUCCESS;
            }else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.INSERT_ERROR;
        }
    }

    /**
     * @description: 修改公告
     * @param: noticeVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/1 20:51
     */
    @RequestMapping("/update")
    public ResultObj update(NoticeVo noticeVo){
        try {
            boolean saveSuccess = noticeService.updateById(noticeVo);
            if (saveSuccess){
                return ResultObj.UPDATE_SUCCESS;
            }else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }
    /**
     * @description: 删除公告
     * @param: id
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/1 20:52
     */
    @RequestMapping("/delete")
    public ResultObj delete(Integer id){
        try {
            boolean removeSuccess = noticeService.removeById(id);
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
     * @description: 批量删除公告
     * @param: noticeVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/1 20:55
     */
    @RequestMapping("/batchDelete")
    public ResultObj batchDelete(NoticeVo noticeVo) {
        try {
            ArrayList<Integer> noticeIds = new ArrayList<>();
            Collections.addAll(noticeIds, noticeVo.getIds());
            boolean removeSuccess = noticeService.removeByIds(noticeIds);
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

