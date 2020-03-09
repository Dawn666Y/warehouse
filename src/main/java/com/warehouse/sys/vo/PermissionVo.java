package com.warehouse.sys.vo;

import com.warehouse.sys.entity.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: warehouse
 * @description: 权限VO
 * @author: Dawn
 * @create: 2020-02-25 21:26
 */
@Data
@EqualsAndHashCode
public class PermissionVo extends Permission {
    private static final long serialVersionUID = -6093932181647206647L;

    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;
}
