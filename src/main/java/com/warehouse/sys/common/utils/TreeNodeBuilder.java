package com.warehouse.sys.common.utils;

import com.warehouse.sys.common.entity.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: warehouse
 * @description: 构建模式处理菜单节点数据
 * @author: Dawn
 * @create: 2020-02-26 18:52
 */
public class TreeNodeBuilder {

    public static List<TreeNode> build(List<TreeNode> treeNodes, Integer pid) {
        List<TreeNode> list = new ArrayList<>();

        for (TreeNode n1 : treeNodes) {
            // 第一遍循环查找一级菜单
            if (n1.getPid().equals(pid))
                list.add(n1);
            // 第二遍循环查找二级菜单
            for (TreeNode n2 : treeNodes)
                if (n2.getPid().equals(n1.getId()))
                    // 找到则添加到一级菜单的子节点
                    n1.getChildren().add(n2);
        }

        return list;
    }
}
