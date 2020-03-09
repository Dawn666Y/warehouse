package com.warehouse.sys.mapper;

import com.warehouse.sys.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Dawn
 * @since 2020-02-25
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    int deleteRolePermissionByPid(@Param("pid") Serializable pid);
}
