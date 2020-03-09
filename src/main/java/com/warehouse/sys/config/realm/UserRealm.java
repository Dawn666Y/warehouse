package com.warehouse.sys.config.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.entity.ActiveUser;
import com.warehouse.sys.entity.Permission;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.IPermissionService;
import com.warehouse.sys.service.IRoleService;
import com.warehouse.sys.service.IUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: warehouse
 * @description: userRealm
 * @author: Dawn
 * @create: 2020-02-24 19:01
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy // 这里是为了注入切面执行懒加载
    private IUserService userService;

    @Autowired
    @Lazy
    private IPermissionService permissionService;

    @Autowired
    @Lazy
    private IRoleService roleService;

    /**
     * 返回类名以供判断
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 授权方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        ActiveUser acticeUser = (ActiveUser) principalCollection.getPrimaryPrincipal();
        User user = acticeUser.getUser();

        List<String> percodes = acticeUser.getPermissions();
        if (DataSourcesConstants.USER_TYPE_SUPER.equals(user.getType())) {
            // 超级管理员拥有所有权限
            simpleAuthorizationInfo.addStringPermission("*:*");
            return simpleAuthorizationInfo;
        } else if (percodes != null && percodes.size() > 0) {
            // 非超级管理员拥有给予的权限
            simpleAuthorizationInfo.addStringPermissions(percodes);
            return simpleAuthorizationInfo;
        }
        return null;
    }

    /**
     * 认证方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname", authenticationToken.getPrincipal().toString());

        User user = userService.getOne(queryWrapper);
        if (null != user) {

            // 查询该用户拥有的权限码
            List<Permission> permissions = new ArrayList<>();
            // 1.构建查询语句
            QueryWrapper<Permission> wrapper = new QueryWrapper<>();

            // sys_permission表中包含权限和菜单，这里只查询权限数据
            wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_PERM);
            // 数据可用状态
            wrapper.eq("available", DataSourcesConstants.AVAILABLE_TRUE);
            // 2.查询用户的角色
            List<Integer> rids = roleService.selectRoleUserByUid(user.getId());
            // 3.查询角色的权限
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
                permissions = permissionService.list(wrapper);
            }
            List<String> percodes = new ArrayList<>();

            for (int i = 0; permissions.size() > 0 && i < permissions.size(); i++) {
                percodes.add(permissions.get(i).getPercode());
            }

            // 填充一个ActiveUser
            ActiveUser activeUser = ActiveUser.builder()
                    .user(user)
                    .permissions(percodes)
                    .build();
            // 取出该user的盐值
            ByteSource bytesSalt = ByteSource.Util.bytes(user.getSalt());

            // 返回
            return new SimpleAuthenticationInfo(activeUser,
                    user.getPwd(),
                    bytesSalt,
                    this.getName());
        }
        return null;
    }
}
