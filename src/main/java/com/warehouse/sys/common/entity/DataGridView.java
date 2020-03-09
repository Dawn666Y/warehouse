package com.warehouse.sys.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: warehouse
 * @description: json数据实体
 * @author: Dawn
 * @create: 2020-02-25 21:06
 */
@Data
@AllArgsConstructor
public class DataGridView {

    // 执行是否成功的代码，layui默认0是成功
    private Integer code = 0;
    // 传递的消息
    private String msg = "操作成功";
    // 数据的长度
    private Long count = 0L;
    // json数据
    private Object data;

    /**
     * @description: 分页返回数据
     * @param: count
     * @param: data
     * @return:
     * @author: Dawn
     * @date: 2020/3/2 16:22
     */
    public DataGridView(Long count, Object data) {
        super();
        this.count = count;
        this.data = data;
    }

    /**
     * @description: 不分页返回数据
     * @param: data
     * @return:
     * @author: Dawn
     * @date: 2020/3/2 16:22
     */
    public DataGridView(Object data) {
        super();
        this.data = data;
    }

    /**
     * @description: 操作失败
     * @param:
     * @return:
     * @author: Dawn
     * @date: 2020/3/2 16:23
     */
    public DataGridView() {
        super();
        this.code = -1;
        this.msg = "操作失败";
    }
}
