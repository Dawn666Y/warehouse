package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.common.entity.TreeNode;
import com.warehouse.sys.entity.Permission;
import com.warehouse.sys.entity.Role;
import com.warehouse.sys.service.IPermissionService;
import com.warehouse.sys.service.IRoleService;
import com.warehouse.sys.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-03-06
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPermissionService permissionService;

    @RequestMapping("/loadAll")
    public DataGridView loadAllRole(RoleVo roleVo) {

        IPage<Role> roleIPage = new Page<>(
                roleVo.getPage(), roleVo.getLimit());
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(roleVo.getName()),
                "name", roleVo.getName());
        wrapper.like(StringUtils.isNotBlank(roleVo.getRemark()),
                "remark", roleVo.getRemark());
        wrapper.ge(roleVo.getStartTime() != null,
                "createtime", roleVo.getStartTime());
        wrapper.le(roleVo.getEndTime() != null,
                "createtime", roleVo.getEndTime());
        wrapper.orderByDesc("createtime");

        roleService.page(roleIPage, wrapper);

        return new DataGridView(roleIPage.getTotal(), roleIPage.getRecords());
    }

    @RequestMapping("/add")
    public ResultObj add(RoleVo roleVo) {
        try {
            // 设置添加时间
            roleVo.setCreatetime(new Date());

            boolean saveSuccess = roleService.save(roleVo);
            if (saveSuccess) {
                return ResultObj.INSERT_SUCCESS;
            } else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.INSERT_ERROR;
        }
    }

    @RequestMapping("/update")
    public ResultObj update(RoleVo roleVo) {
        try {
            boolean saveSuccess = roleService.updateById(roleVo);
            if (saveSuccess) {
                return ResultObj.UPDATE_SUCCESS;
            } else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    @RequestMapping("/delete")
    public ResultObj delete(Integer id) {
        try {
            boolean removeSuccess = roleService.removeById(id);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("/loadPermissionDTree")
    public DataGridView loadPermissionDTree(Integer rid) {
        // 查询当前所有可用的菜单和权限
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("available", DataSourcesConstants.AVAILABLE_TRUE);
        List<Permission> permissions = permissionService.list(wrapper);

        // 查询当前角色拥有的所有权限
        // 1. 先查询出角色拥有的所有权限ID
        List<Integer> hasPermissionsId =
                roleService.selectRolePermissionByRid(rid);
        // 2. 根据这些id查询permission表拿到数据
        List<Permission> hasPermissions = new ArrayList<>();
        // 如果该角色拥有权限则查询，如果没有则是个空list
        if (hasPermissionsId != null && hasPermissionsId.size() > 0) {
            wrapper.in("id", hasPermissionsId);
            hasPermissions = permissionService.list(wrapper);
        }

        // 拼接TreeNode
        List<TreeNode> nodes = new ArrayList<>();
        for (Permission allPermission : permissions) {
            String checkArr = "0";
            for (Permission hasPermission : hasPermissions) {
                if (allPermission.getId().equals(hasPermission.getId())) {
                    checkArr = "1";
                    break;
                }
            }
            nodes.add(new TreeNode(allPermission.getId(),
                    allPermission.getPid(),
                    allPermission.getTitle(),
                    allPermission.getOpen() != null
                            && allPermission.getOpen() == 1,
                    checkArr));
        }
        return new DataGridView(nodes);
    }

    @RequestMapping("/saveRolePermission")
    public ResultObj saveRolePermission(Integer rid, Integer[] ids) {
        try {
            if (ids != null && ids.length > 0) {
                boolean saveSuccess = roleService.saveRolePermission(rid, ids);
                if (saveSuccess)
                    return ResultObj.DISPATCH_SUCCESS;
                else
                    throw new Exception();
            } else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}

