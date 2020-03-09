package com.warehouse.sys.service.impl;

import com.warehouse.sys.entity.Role;
import com.warehouse.sys.mapper.RoleMapper;
import com.warehouse.sys.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Dawn
 * @since 2020-03-06
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public boolean removeById(Serializable id) {
        baseMapper.deleteRolePermissionByRid(id);
        baseMapper.deleteRoleUserByRid(id);
        return super.removeById(id);
    }

    @Override
    public List<Integer> selectRolePermissionByRid(Integer rid) {
        // 查询角色含有的权限id
        return baseMapper.selectRolePermissionByRid(rid);
    }

    @Override
    public boolean saveRolePermission(Integer rid, Integer[] ids) {
        // 先移除这个角色id下的全部权限
        baseMapper.deleteRolePermissionByRid(rid);

        // 定义变量用来记录插入成功的条数
        int saveSuccess = 0;
        for (Integer pid : ids) {
            int row = baseMapper.saveRolePermission(rid, pid);
            // 每次插入成功增1
            saveSuccess = row == 0 ? saveSuccess : saveSuccess + 1;
        }
        // 判断是否全部插入成功，有一条未成功则返回false
        return saveSuccess == ids.length;
    }

    @Override
    public List<Integer> selectRoleUserByUid(Serializable uid) {
        return baseMapper.selectRoleUserByUid(uid);
    }

    @Override
    public boolean saveRoleUser(Serializable uid, Integer[] rids) {
        // 删除该用户的所有角色
        baseMapper.deleteRoleUserByUid(uid);

        // 定义变量用来记录插入成功的条数
        int saveSuccess = 0;
        for (Integer rid : rids) {
            int row = baseMapper.saveRoleUser(uid, rid);
            // 每次插入成功增1
            saveSuccess = row == 0 ? saveSuccess : saveSuccess + 1;
        }
        // 判断是否全部插入成功，有一条未成功则返回false
        return saveSuccess == rids.length;
    }
}
