package com.warehouse.sys.service.impl;

import com.warehouse.sys.entity.Permission;
import com.warehouse.sys.mapper.PermissionMapper;
import com.warehouse.sys.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Dawn
 * @since 2020-02-25
 */
@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    @Override
    public boolean removeById(Serializable id) {
        // 删除角色权限关系表中的pid对应的所有数据
        int deleteById = baseMapper.deleteRolePermissionByPid(id);
        return super.removeById(id);
    }
}
