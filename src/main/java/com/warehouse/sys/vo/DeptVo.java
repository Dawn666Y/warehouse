package com.warehouse.sys.vo;

import com.warehouse.sys.entity.Dept;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: warehouse
 * @description: 部门vo
 * @author: Dawn
 * @create: 2020-03-02 16:05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeptVo extends Dept {

    private static final long serialVersionUID = 1705884098582798583L;
    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;
}
