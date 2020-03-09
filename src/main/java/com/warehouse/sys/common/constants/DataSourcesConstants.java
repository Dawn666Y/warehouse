package com.warehouse.sys.common.constants;

/**
 * @program: warehouse
 * @description: 数据库的一些常量
 * @author: Dawn
 * @create: 2020-02-25 21:38
 */
public interface DataSourcesConstants {
    // sys_permission的type字段两个取值
    String SYS_PERMISSION_TYPE_MENU ="menu";
    String SYS_PERMISSION_TYPE_PERM ="permission";

    // sys_permission的数据是否可用，1可用，0不可用
    Integer AVAILABLE_TRUE=1;
    Integer AVAILABLE_FALSE=0;

    // sys_user的type 用户类型，1超级管理员，0普通用户
    Integer USER_TYPE_NORMAL=1;
    Integer USER_TYPE_SUPER=0;

    // sys_permission的菜单是否展开，1展开，0不展开
    Integer MENU_OPEN=1;

    // sys_dept是否默认展开，1展开，0不展开
    Integer DEPT_OPEN=1;
    String USER_DEFAULT_PWD = "123456";
}
