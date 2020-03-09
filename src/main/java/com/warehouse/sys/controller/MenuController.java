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
import com.warehouse.sys.service.IRoleService;
import com.warehouse.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: warehouse
 * @description: 菜单控制器
 * @author: Dawn
 * @create: 2020-02-25 21:04
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IPermissionService permissionService;

    @Autowired
    private IRoleService roleService;

    /**
     * @description: 加载登录后左边的菜单
     * @param: permissionVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/3/4 12:11
     */
    @GetMapping("/loadLeftMenuJson")
    public DataGridView loadLeftMenuJson(PermissionVo permissionVo) {

        // 获取当前用户的信息
        User user = (User) WebUtils.getSession()
                .getAttribute(WebConstants.USER_ATTRIBUTE);

        // 构建查询语句
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();

        // sys_permission表中包含权限和菜单，这里只查询菜单数据
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_MENU);
        // 数据可用状态
        wrapper.eq("available", DataSourcesConstants.AVAILABLE_TRUE);

        // 查询
        List<Permission> menu = new ArrayList<>();
        // 判断用户类型
        if (DataSourcesConstants.USER_TYPE_SUPER.equals(user.getType()))
            // 是超级管理员则直接查询
            menu = permissionService.list(wrapper);
        else {
            // 不是超级管理员则按照用户-角色-权限查询
            // 查询用户的角色
            List<Integer> rids = roleService.selectRoleUserByUid(user.getId());
            // 查询角色的权限
            List<Integer> permissionIds = new ArrayList<>();
            for (Integer rid : rids) {
                permissionIds =
                        roleService.selectRolePermissionByRid(rid)
                                .stream()
                                .distinct() // jdk8 stream去重
                                .collect(Collectors.toList());
            }
            if (permissionIds.size() > 0) {
                wrapper.in("id", permissionIds);
                menu = permissionService.list(wrapper);
            }
        }
        // 把permission数据转成有层次结构的菜单
        List<TreeNode> finalMenu = new ArrayList<>();
        if (menu.size() > 0) {
            // 初始化菜单节点
            List<TreeNode> nodes = new ArrayList<>();
            menu.forEach(i ->
                    // 此处使用了lombok提供的builder初始化方法
                    nodes.add(TreeNode.builder()
                            .id(i.getId())
                            .pid(i.getPid())
                            .title(i.getTitle())
                            .icon(i.getIcon())
                            .href(i.getHref())
                            // 菜单是否展开
                            .spread(i.getOpen()
                                    .equals(DataSourcesConstants.MENU_OPEN))
                            .children(new ArrayList<>())
                            .build())
            );
            // 排列菜单层次
            finalMenu = TreeNodeBuilder.build(nodes, 1);
        }
        return new DataGridView(finalMenu);
    }

    /*********以下为菜单管理*********/

    @RequestMapping("/loadMenuDTree")
    public DataGridView loadMenuDTree() {
        // 查询菜单数据
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_MENU);
        List<Permission> menuList = permissionService.list(wrapper);

        if (menuList != null && menuList.size() > 0) {

            List<TreeNode> treeNodes = new ArrayList<>();

            for (Permission menu : menuList) {
                treeNodes.add(new TreeNode(
                        menu.getId(),
                        menu.getPid(),
                        menu.getTitle(),
                        menu.getOpen().equals(DataSourcesConstants.DEPT_OPEN)
                ));
            }
            return new DataGridView(treeNodes);
        }
        return new DataGridView();
    }

    /**
     * @description: 查询菜单
     * @param: permissionVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/3/4 12:20
     */
    @RequestMapping("/loadAll")
    public DataGridView loadAll(PermissionVo permissionVo) {
        IPage<Permission> permissionIPage = new Page<>(
                permissionVo.getPage(), permissionVo.getLimit());
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        // 只查询菜单
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_MENU)
                // 按照菜单名称查询
                .and(StringUtils.isNotBlank(permissionVo.getTitle()), i -> i.like(
                        "title", permissionVo.getTitle()))
                // 根据id返回该菜单自己及其子菜单
                .and(permissionVo.getId() != null, i -> i.eq("id",
                        permissionVo.getId())
                        .or()
                        .eq("pid",
                                permissionVo.getId()));
        /* 下面这种查询方式会导致生成 type='menu' and id=? or pid=?
         * 这会导致查询结果将包含pid字段等于permissionVo.id的权限数据
         * 而我们的目标是type='menu' and (id=? or pid=?)
         * 要把type是菜单的这一条件固定下来
         * 所以选择采取上面的这种方案
         * */
        /*wrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()),
                "title", permissionVo.getTitle());

        wrapper.eq(permissionVo.getId() != null, "id",
                permissionVo.getId())
                .or()
                .eq(permissionVo.getId() != null, "pid",
                        permissionVo.getId());*/

        // ordernum是自定义的修改菜单排序的重要字段
        wrapper.orderByAsc("ordernum");

        permissionService.page(permissionIPage, wrapper);
        return new DataGridView(permissionIPage.getTotal(),
                permissionIPage.getRecords());
    }

    /**
     * @description: 添加菜单
     * @param: permissionVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/4 12:22
     */
    @RequestMapping("/add")
    public ResultObj add(PermissionVo permissionVo) {
        try {
            // 设置添加类型
            permissionVo.setType(DataSourcesConstants.SYS_PERMISSION_TYPE_MENU);
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

    @RequestMapping("/loadMenuMaxOrderNum")
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

    @RequestMapping("/checkMenuHasChild")
    public Map<String, Object> checkPermissionHasChild(PermissionVo permissionVo) {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();

        // 是否有节点依赖本节点
        wrapper.eq("pid", permissionVo.getId());

        List<Permission> checkList = permissionService.list(wrapper);

        map.put("value", checkList != null && checkList.size() > 0);
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

    /**********菜单管理结束**********/
}
