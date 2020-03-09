package com.warehouse.sys.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: warehouse
 * @description: 菜单数据节点实体类
 * @author: Dawn
 * @create: 2020-02-26 18:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode {

    private Integer id;
    // 封装json时使用别名
    @JsonProperty("parentId")
    private Integer pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private List<TreeNode> children;

    // dtree复选树里的选中节点属性，0不选中，1选中
    private String checkArr = "0";

    /**
     * @description: 提供给dtree解析的数据格式
     * @param: id
     * @param: pid
     * @param: title
     * @param: spread
     * @return: com.warehouse.sys.common.entity.TreeNode
     * @author: Dawn
     * @date: 2020/3/2 15:30
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
    }

    /**
     * @description: dtree复选树的构造器
     * @param: id
     * @param: pid
     * @param: title
     * @param: spread
     * @param: checkAttr
     * @return: com.warehouse.sys.common.entity.TreeNode
     * @author: Dawn
     * @date: 2020/3/6 13:38
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread, String checkArr) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
        this.checkArr = checkArr;
    }
}
