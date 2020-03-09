package com.warehouse.sys.service.impl;

import com.warehouse.sys.entity.User;
import com.warehouse.sys.mapper.RoleMapper;
import com.warehouse.sys.mapper.UserMapper;
import com.warehouse.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Dawn
 * @since 2020-02-24
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        // 根据用户id删除他的角色信息
        roleMapper.deleteRoleUserByUid(id);

        return super.removeById(id);
    }

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }
}
