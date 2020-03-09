package com.warehouse.sys.mapper;

import com.warehouse.sys.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Dawn
 * @since 2020-03-06
 */
public interface RoleMapper extends BaseMapper<Role> {

    void deleteRolePermissionByRid(@Param("rid") Serializable rid);

    void deleteRoleUserByRid(@Param("rid") Serializable rid);

    List<Integer> selectRolePermissionByRid(@Param("rid") Integer rid);

    int saveRolePermission(@Param("rid") Integer rid,@Param("pid") Integer pid);

    void deleteRoleUserByUid(@Param("uid") Serializable uid);

    List<Integer> selectRoleUserByUid(@Param("uid") Serializable uid);

    int saveRoleUser(@Param("uid") Serializable uid, @Param("rid") Integer rid);
}
