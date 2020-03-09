package com.warehouse.sys.common.entity;

import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.constants.WebConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: warehouse
 * @description:
 * @author: Dawn
 * @create: 2020-02-25 16:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    public static final ResultObj LOGIN_SUCCESS =
            new ResultObj(WebConstants.OK, "登录成功！");

    public static final ResultObj LOGIN_ERROR =
            new ResultObj(WebConstants.ERROR, "登录失败！请检查用户名和密码！");

    public static final ResultObj DELETE_SUCCESS =
            new ResultObj(WebConstants.OK, "删除成功！");

    public static final ResultObj DELETE_ERROR =
            new ResultObj(WebConstants.ERROR, "删除失败！");

    public static final ResultObj INSERT_SUCCESS =
            new ResultObj(WebConstants.OK, "添加成功！");

    public static final ResultObj INSERT_ERROR =
            new ResultObj(WebConstants.ERROR, "添加失败！");

    public static final ResultObj UPDATE_SUCCESS =
            new ResultObj(WebConstants.OK, "更新成功！");

    public static final ResultObj UPDATE_ERROR =
            new ResultObj(WebConstants.ERROR, "更新失败！");

    public static final ResultObj DISPATCH_SUCCESS =
            new ResultObj(WebConstants.OK, "分配成功！");

    public static final ResultObj DISPATCH_ERROR =
            new ResultObj(WebConstants.ERROR, "分配失败！");

    public static final ResultObj RESET_PWD_SUCCESS =
            new ResultObj(WebConstants.OK, "重置密码成功！密码为"
                    + DataSourcesConstants.USER_DEFAULT_PWD);

    public static final ResultObj RESET_PWD_ERROR =
            new ResultObj(WebConstants.ERROR, "重置密码失败！");

    private Integer code;
    private String msg;
}
