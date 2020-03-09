package com.warehouse.sys.service;

import com.warehouse.sys.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Dawn
 * @since 2020-03-06
 */
public interface IRoleService extends IService<Role> {

    List<Integer> selectRolePermissionByRid(Integer rid);

    boolean saveRolePermission(Integer rid, Integer[] ids);

    List<Integer> selectRoleUserByUid(Serializable id);

    boolean saveRoleUser(Serializable uid, Integer[] rids);
}
