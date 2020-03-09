package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.constants.WebConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.common.entity.TreeNode;
import com.warehouse.sys.common.utils.TreeNodeBuilder;
import com.warehouse.sys.common.utils.WebUtils;
import com.warehouse.sys.entity.Permission;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.IPermissionService;
import com.warehouse.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-25
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @RequestMapping("/loadMenuDTree")
    public DataGridView loadMenuDTree() {
        // 查询权限数据
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_MENU);
        List<Permission> permissionList = permissionService.list(wrapper);

        if (permissionList != null && permissionList.size() > 0) {

            List<TreeNode> treeNodes = new ArrayList<>();

            for (Permission permission : permissionList) {
                treeNodes.add(new TreeNode(
                        permission.getId(),
                        permission.getPid(),
                        permission.getTitle(),
                        permission.getOpen().equals(DataSourcesConstants.DEPT_OPEN)
                ));
            }
            return new DataGridView(treeNodes);
        }
        return new DataGridView();
    }

    @RequestMapping("/loadAll")
    public DataGridView loadAll(PermissionVo permissionVo) {
        IPage<Permission> permissionIPage = new Page<>(
                permissionVo.getPage(), permissionVo.getLimit());
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        // 只查询权限
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_PERM)
                // 按照权限名称查询
                .and(StringUtils.isNotBlank(permissionVo.getTitle()), i -> i.like(
                        "title", permissionVo.getTitle()))
                .and(StringUtils.isNotBlank(permissionVo.getPercode()), i -> i.like(
                        "percode", permissionVo.getPercode()
                ))
                // 根据id返回该权限自己及其子权限
                .and(permissionVo.getId() != null, i -> i.eq("pid",
                                permissionVo.getId()));
        // ordernum是自定义的修改权限排序的重要字段
        wrapper.orderByAsc("ordernum");

        permissionService.page(permissionIPage, wrapper);
        return new DataGridView(permissionIPage.getTotal(), permissionIPage.getRecords());
    }

    @RequestMapping("/add")
    public ResultObj add(PermissionVo permissionVo) {
        try {
            // 设置添加类型
            permissionVo.setType(DataSourcesConstants.SYS_PERMISSION_TYPE_PERM);
            boolean saveSuccess = permissionService.save(permissionVo);
            if (saveSuccess) {
                return ResultObj.INSERT_SUCCESS;
            } else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.INSERT_ERROR;
        }
    }

    @RequestMapping("/update")
    public ResultObj update(PermissionVo permissionVo) {
        try {
            boolean updateSuccess = permissionService.updateById(permissionVo);
            if (!updateSuccess) throw new Exception();
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    @RequestMapping("/loadPermissionMaxOrderNum")
    public Map<String, Object> loadPermissionMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ordernum");
        List<Permission> permissionList = permissionService.list(wrapper);
        if (permissionList != null && permissionList.size() > 0)
            map.put("ordernum", permissionList.get(0).getOrdernum());
        else
            // 证明sys_permission没有数据
            map.put("ordernum", 0);
        return map;
    }

    @RequestMapping("/delete")
    public ResultObj delete(Integer id) {
        try {
            boolean deleteSuccess = permissionService.removeById(id);
            if (!deleteSuccess) throw new Exception();
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}

