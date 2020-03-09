package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.ResultObj;
import com.warehouse.sys.common.entity.TreeNode;
import com.warehouse.sys.entity.Dept;
import com.warehouse.sys.service.IDeptService;
import com.warehouse.sys.vo.DeptVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-03-02
 */
@RestController
@RequestMapping("/dept")
public class DeptController {
    @Autowired
    private IDeptService deptService;

    /**
     * @description: 查询所有部门数据
     * @param: deptVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/3/2 16:13
     */
    @RequestMapping("/loadDeptDTree")
    public DataGridView loadDeptDTree() {
        // 查询部门数据
        List<Dept> deptList = deptService.list();

        if (deptList != null && deptList.size() > 0) {

            List<TreeNode> treeNodes = new ArrayList<>();

            for (Dept dept : deptList) {
                treeNodes.add(new TreeNode(
                        dept.getId(),
                        dept.getPid(),
                        dept.getTitle(),
                        dept.getOpen().equals(DataSourcesConstants.DEPT_OPEN)
                ));
            }
            return new DataGridView(treeNodes);
        }
        return new DataGridView();
    }

    @RequestMapping("/loadAll")
    public DataGridView loadAll(DeptVo deptVo) {
        IPage<Dept> deptIPage = new Page<>(deptVo.getPage(), deptVo.getLimit());
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(deptVo.getTitle()),
                "title", deptVo.getTitle());
        wrapper.like(StringUtils.isNotBlank(deptVo.getRemark()),
                "remark", deptVo.getRemark());
        wrapper.like(StringUtils.isNotBlank(deptVo.getAddress()),
                "address", deptVo.getAddress());

        // 根据id返回该部门自己及其子部门
        wrapper.eq(
                deptVo.getId() != null, "id", deptVo.getId())
                .or()
                .eq(deptVo.getId() != null, "pid",
                        deptVo.getId());

        // ordernum是自定义的修改部门排序的重要字段
        wrapper.orderByAsc("ordernum");

        deptService.page(deptIPage, wrapper);
        return new DataGridView(deptIPage.getTotal(), deptIPage.getRecords());
    }

    /**
     * @description: 添加部门
     * @param: deptVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/3 12:59
     */
    @RequestMapping("/add")
    public ResultObj add(DeptVo deptVo) {
        try {
            deptVo.setCreatetime(new Date());
            boolean saveSuccess = deptService.save(deptVo);
            if (saveSuccess) {
                return ResultObj.INSERT_SUCCESS;
            } else throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.INSERT_ERROR;
        }
    }

    /**
     * @description: 更新部门
     * @param: deptVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/3 12:59
     */
    @RequestMapping("/update")
    public ResultObj update(DeptVo deptVo) {
        try {
            boolean updateSuccess = deptService.updateById(deptVo);
            if (!updateSuccess) throw new Exception();
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * @description: 获取当前最大的排序码返回
     * @param: map
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: Dawn
     * @date: 2020/3/3 13:01
     */
    @RequestMapping("/loadDeptMaxOrderNum")
    public Map<String, Object> loadDeptMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("ordernum");
        List<Dept> deptList = deptService.list(wrapper);
        if (deptList != null && deptList.size() > 0)
            map.put("ordernum", deptList.get(0).getOrdernum());
        else
            // 证明sys_dept没有数据
            map.put("ordernum", 0);
        return map;
    }

    /**
     * @description: 检查节点是否被依赖
     * @param: deptVo
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: Dawn
     * @date: 2020/3/3 18:07
     */
    @RequestMapping("/checkDeptHasChild")
    public Map<String, Object> checkDeptHasChild(DeptVo deptVo) {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();

        // 是否有节点依赖本节点
        wrapper.eq("pid", deptVo.getId());

        List<Dept> checkList = deptService.list(wrapper);

        map.put("value", checkList != null && checkList.size() > 0);
        return map;
    }

    /**
     * @description: 删除部门
     * @param: deptVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/3/3 18:19
     */
    @RequestMapping("/delete")
    public ResultObj delete(Integer id) {
        try {
            boolean deleteSuccess = deptService.removeById(id);
            if (!deleteSuccess) throw new Exception();
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}

