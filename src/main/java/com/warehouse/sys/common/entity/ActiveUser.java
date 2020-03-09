package com.warehouse.sys.common.entity;

import com.warehouse.sys.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: warehouse
 * @description: 登录的用户
 * @author: Dawn
 * @create: 2020-02-24 18:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveUser {

    // 登录用户
    private User user;

    // 角色
    private List<String> roles;

    // 权限
    private List<String> permissions;

}
