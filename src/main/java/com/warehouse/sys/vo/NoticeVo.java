package com.warehouse.sys.vo;

import com.warehouse.sys.entity.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: warehouse
 * @description: 公告vo
 * @author: Dawn
 * @create: 2020-02-29 20:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeVo extends Notice {

    private static final long serialVersionUID = -1000745595303146747L;

    // 查询的页数
    private Integer page = 1;

    // 每页上限
    private Integer limit = 10;

    private Integer[] ids;

    // 查询范围：起始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    // 查询范围：终止时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
