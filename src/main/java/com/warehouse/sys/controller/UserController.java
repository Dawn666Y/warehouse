package com.warehouse.sys.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.common.utils.PinyinUtils;
import com.warehouse.sys.entity.Dept;
import com.warehouse.sys.entity.Role;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.IDeptService;
import com.warehouse.sys.service.IRoleService;
import com.warehouse.sys.service.IUserService;
import com.warehouse.sys.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-24
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IDeptService deptService;

    @Autowired
    private IRoleService roleService;

    @RequestMapping("/loadAll")
    public DataGridView loadAll(UserVo userVo) {
        IPage<User> userPage =
                new Page<>(userVo.getPage(), userVo.getLimit());

        // 查询权限数据
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 用户登录名、姓名条件
        wrapper.like(StringUtils.isNotBlank(userVo.getName()),
                "loginname", userVo.getName())
                .or().like(StringUtils.isNotBlank(userVo.getName()),
                "name", userVo.getName());
        // 用户地址条件
        wrapper.like(StringUtils.isNotBlank(userVo.getAddress()),
                "address", userVo.getAddress());
        // 部门条件
        wrapper.eq(userVo.getDeptid() != null,
                "deptid", userVo.getDeptid());

        // 只查询非超级管理员用户
        wrapper.eq("type", DataSourcesConstants.USER_TYPE_NORMAL);
//        .and(StringUtils.isNotBlank(userVo.getName()),
//                i->i.like());

        userService.page(userPage, wrapper);

        // 填充部门名称和上级姓名
        List<User> users = userPage.getRecords();
        for (User user : users) {
            Integer did = user.getDeptid();
            if (did != null) {
                Dept one = deptService.getById(did);
                user.setDeptname(one.getTitle());
            }
            Integer mgr = user.getMgr();
            if (mgr != null) {
                User one = userService.getById(mgr);
                user.setLeadername(one.getName());
            }
        }

        return new DataGridView(userPage.getTotal(), users);
    }

    @RequestMapping("/loadUserMaxOrderNum")
    public Map<String, Object> loadUserMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ordernum");
        List<User> userList = userService.list(wrapper);
        if (userList != null && userList.size() > 0)
            map.put("ordernum", userList.get(0).getOrdernum());
        else
            // 证明sys_user没有数据
            map.put("ordernum", 0);
        return map;
    }

    @RequestMapping("/loadUsersByDeptId")
    public DataGridView loadUsersByDeptId(Integer deptid) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(deptid != null, "deptid", deptid);
        wrapper.eq("type", DataSourcesConstants.USER_TYPE_NORMAL);
        List<User> list = userService.list(wrapper);
        if (list != null && list.size() > 0)
            return new DataGridView(list);
        else
            return null;
    }

    @RequestMapping("/pinyin")
    public Map<String, Object> pinyin(String name) {
        Map<String, Object> map = new HashMap<>();
        String pingYin = PinyinUtils.getPingYin(name);
        map.put("value", pingYin);
        return map;
    }

    @RequestMapping("/add")
    public ResultObj add(UserVo userVo) {
        try {
            // pwd=null,hiredate=null,type=null,salt=null,需要设置

            // 生成一个盐值
            String salt = IdUtil.simpleUUID().toUpperCase();
            // 根据盐值生成密码
            String pwd = new Md5Hash( // shiro中的MD5加密方法
                    DataSourcesConstants.USER_DEFAULT_PWD, // 默认密码
                    salt, // 盐值
                    2) // 迭代次数
                    .toString(); // 转成字符串

            // 填充字段
            userVo.setPwd(pwd);
            userVo.setHiredate(new Date());
            userVo.setType(DataSourcesConstants.USER_TYPE_NORMAL);
            userVo.setSalt(salt);
            boolean saveSuccess = userService.save(userVo);
            if (saveSuccess)
                return ResultObj.INSERT_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.INSERT_ERROR;
        }
    }

    @RequestMapping("/loadUserById")
    public Map<String, Object> loadUserById(Integer id) {
        Map<String, Object> map = new HashMap<>();
        User byId = userService.getById(id);
        map.put("user", byId);
        return map;
    }

    @RequestMapping("/update")
    public ResultObj update(UserVo userVo) {
        try {
//            System.out.println("=前台传来userVo=" + userVo.toString());
            boolean saveSuccess = userService.updateById(userVo);
            if (saveSuccess)
                return ResultObj.UPDATE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    @RequestMapping("/hasSubordinate")
    public Map<String, Object> hasSubordinate(Integer id) {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(id != null, "mgr", id);
        List<User> list = userService.list(wrapper);

        map.put("value", list != null && list.size() > 0);
        return map;
    }

    @RequestMapping("/delete")
    public ResultObj delete(Integer id) {
        try {

            boolean removeSuccess = userService.removeById(id);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("/resetPwd")
    public ResultObj resetPwd(Integer id) {
        User user = new User();
        user.setId(id);
        // 生成一个盐值
        String salt = IdUtil.simpleUUID().toUpperCase();
        // 根据盐值生成密码
        String pwd = new Md5Hash( // shiro中的MD5加密方法
                DataSourcesConstants.USER_DEFAULT_PWD, // 默认密码
                salt, // 盐值
                2) // 迭代次数
                .toString(); // 转成字符串

        // 填充字段
        user.setPwd(pwd);
        user.setSalt(salt);
        try {
//            System.out.println("==========user = " + user.toString());
            boolean updateSuccess = userService.updateById(user);
            if (updateSuccess)
                return ResultObj.RESET_PWD_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.RESET_PWD_ERROR;
        }
    }

    @RequestMapping("/initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id) {

        // 查询当前所有可用的角色
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("available", DataSourcesConstants.AVAILABLE_TRUE);
        List<Map<String, Object>> maps = roleService.listMaps(wrapper);

        // 查询当前用户已经拥有的角色
        List<Integer> rids = roleService.selectRoleUserByUid(id);
        for (Map<String, Object> map : maps) {
            boolean LAY_CHECKED = false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : rids) {
                if (rid.equals(roleId)) {
                    LAY_CHECKED = true;
                    break;
                }
            }
            map.put("LAY_CHECKED", LAY_CHECKED);
        }
        return new DataGridView((long) maps.size(), maps);
    }

    @RequestMapping("/saveRoleUser")
    public ResultObj saveRoleUser(Integer uid,Integer[] ids){
        try {
            boolean saveSuccess=roleService.saveRoleUser(uid,ids);
            if (saveSuccess)
                return ResultObj.DISPATCH_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}

