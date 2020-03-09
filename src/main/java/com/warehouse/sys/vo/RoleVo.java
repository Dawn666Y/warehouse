package com.warehouse.sys.vo;

import com.warehouse.sys.entity.Role;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: warehouse
 * @description: 角色vo
 * @author: Dawn
 * @create: 2020-03-06 11:15
 */
@Data
public class RoleVo extends Role {
    private static final long serialVersionUID = -4437726165749403263L;

    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;


    // 查询范围：起始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    // 查询范围：终止时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
