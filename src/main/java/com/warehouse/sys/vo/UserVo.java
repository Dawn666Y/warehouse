package com.warehouse.sys.vo;

import com.warehouse.sys.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @program: warehouse
 * @description: 用户vo
 * @author: Dawn
 * @create: 2020-03-06 18:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class UserVo extends User {
    private static final long serialVersionUID = -2292683995542827470L;

    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;

}
