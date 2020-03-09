package com.warehouse.sys.vo;

import com.warehouse.sys.entity.LogLogin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: warehouse
 * @description: 登录日志
 * @author: Dawn
 * @create: 2020-02-28 17:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LogLoginVo{

    private static final long serialVersionUID = 4251464533845561058L;

    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;

    private Integer[] ids;
    private Integer id;

    private String loginname;

    private String loginip;

    private Date logintime;
    // 查询范围：起始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    // 查询范围：终止时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
