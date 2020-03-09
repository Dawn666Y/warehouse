package com.warehouse.sys.cache;

import com.warehouse.sys.entity.Dept;
import com.warehouse.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: warehouse
 * @description: 缓存切面
 * @author: Dawn
 * @create: 2020-03-03 18:58
 */
@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j // 日志
public class CacheAspect {

    // 声明一个缓存容器
    private final Map<String, Object> CACHE_CONTAINER = new HashMap<>();

    // 声明切面表达式
    public static final String POINTCUT_DEPT_INSERT =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.save(..))";
    public static final String POINTCUT_DEPT_UPDATE =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.updateById(..))";
    public static final String POINTCUT_DEPT_GET =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.getById(..))";
    public static final String POINTCUT_DEPT_REMOVE =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.removeById(..))";

    public static final String CACHE_DEPT_PREFIX = "dept:";

    /**
     * @description: 查询切入
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:30
     */
    @Around(value = POINTCUT_DEPT_GET)
    public Object cacheDeptGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 查询缓存中是否有要查询的数据
        Object res1 = CACHE_CONTAINER.get(CACHE_DEPT_PREFIX + object);
        // 有则返回
        if (null != res1) {
            log.info("从缓存中找到部门对象：" + res1.toString());
            return res1;
        } else {
            log.info("未在缓存中找到该部门，查询了数据库");
            // 没有的话取数据库中查询
            Dept res2 = (Dept) joinPoint.proceed();
            // key值取前缀+数据的id
            CACHE_CONTAINER.put(CACHE_DEPT_PREFIX + res2.getId(), res2);
            return res2;
        }
    }

    /**
     * @description: 添加同步缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/6 20:12
     */
    @Around(value = POINTCUT_DEPT_INSERT)
    public Object cacheDeptInsert(ProceedingJoinPoint joinPoint) throws Throwable {
        Dept object = (Dept) joinPoint.getArgs()[0];
        boolean proceed = (boolean) joinPoint.proceed();
        if (proceed) {
            CACHE_CONTAINER.put(CACHE_DEPT_PREFIX + object.getId(), object);
            log.info("缓存插入部门数据成功：" + CACHE_DEPT_PREFIX + object.getId()
                    + object.toString());
        } else
            log.info("缓存插入部门数据失败：" + CACHE_DEPT_PREFIX + object.getId()
                    + object.toString());
        return proceed;
    }

    /**
     * @description: 更新同步缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:44
     */
    @Around(value = POINTCUT_DEPT_UPDATE)
    public Object cacheDeptUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Dept dept = (Dept) joinPoint.getArgs()[0];
        Boolean updateSuccess = (Boolean) joinPoint.proceed();
        if (updateSuccess) {
            // 数据库更新成功，更新缓存
            // 取出数据
            Dept oldDept = (Dept) CACHE_CONTAINER.get(CACHE_DEPT_PREFIX
                    + dept.getId());
            // 如果缓存中没有就new一个
            Dept newDept = oldDept == null ? new Dept() : oldDept;
            // 复制更新后的数据
            BeanUtils.copyProperties(dept, newDept);
            // 更新数据缓存
            log.info("部门缓存已经更新：" + CACHE_DEPT_PREFIX + newDept.getId()
                    + newDept.toString());
            CACHE_CONTAINER.put(CACHE_DEPT_PREFIX + newDept.getId(), newDept);

        }
        return updateSuccess;
    }

    /**
     * @description: 删除缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:53
     */
    @Around(value = POINTCUT_DEPT_REMOVE)
    public Object cacheDeptRemove(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean removeSuccess = (Boolean) joinPoint.proceed();
        if (removeSuccess) {
            // 数据库删除成功，移除缓存中的数据
            log.info("部门缓存已经删除：" + CACHE_DEPT_PREFIX + id);
            CACHE_CONTAINER.remove(CACHE_DEPT_PREFIX + id);
        }
        return removeSuccess;
    }

    // 声明切面表达式
    public static final String POINTCUT_USER_INSERT =
            "execution(* com.warehouse.sys.service.impl.UserServiceImpl.save(..))";
    public static final String POINTCUT_USER_UPDATE =
            "execution(* com.warehouse.sys.service.impl.UserServiceImpl.updateById(..))";
    public static final String POINTCUT_USER_GET =
            "execution(* com.warehouse.sys.service.impl.UserServiceImpl.getById(..))";
    public static final String POINTCUT_USER_REMOVE =
            "execution(* com.warehouse.sys.service.impl.UserServiceImpl.removeById(..))";

    public static final String CACHE_USER_PREFIX = "user:";

    /**
     * @description: 查询切入
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:30
     */
    @Around(value = POINTCUT_USER_GET)
    public Object cacheUserGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 查询缓存中是否有要查询的数据
        Object res1 = CACHE_CONTAINER.get(CACHE_USER_PREFIX + object);
        // 有则返回
        if (null != res1) {
            log.info("从缓存中找到用户对象：" + res1.toString());
            return res1;
        } else {
            log.info("未在缓存中找到该用户，查询了数据库");
            // 没有的话取数据库中查询
            User res2 = (User) joinPoint.proceed();
            // key值取前缀+数据的id
            CACHE_CONTAINER.put(CACHE_USER_PREFIX + res2.getId(), res2);
            return res2;
        }
    }

    /**
     * @description: 添加同步缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/6 20:12
     */
    @Around(value = POINTCUT_USER_INSERT)
    public Object cacheUserInsert(ProceedingJoinPoint joinPoint) throws Throwable {
        User object = (User) joinPoint.getArgs()[0];
        boolean proceed = (boolean) joinPoint.proceed();
        if (proceed) {
            CACHE_CONTAINER.put(CACHE_USER_PREFIX + object.getId(), object);
            log.info("缓存插入用户数据成功：" + CACHE_USER_PREFIX + object.getId()
                    + object.toString());
        } else
            log.info("缓存插入用户数据失败：" + CACHE_USER_PREFIX + object.getId()
                    + object.toString());
        return proceed;
    }

    /**
     * @description: 更新同步缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:44
     */
    @Around(value = POINTCUT_USER_UPDATE)
    public Object cacheUserUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        User user = (User) joinPoint.getArgs()[0];
        Boolean updateSuccess = (Boolean) joinPoint.proceed();
        if (updateSuccess) {
            // 数据库更新成功，更新缓存
            // 取出数据
            User oldUser = (User) CACHE_CONTAINER.get(CACHE_USER_PREFIX
                    + user.getId());
            // 如果缓存中没有就new一个
            User newUser = oldUser == null ? new User() : oldUser;
            // 复制更新后的数据
            BeanUtils.copyProperties(user, newUser);
            // 更新数据缓存
            log.info("用户缓存已经更新：" + CACHE_USER_PREFIX + newUser.getId()
                    + newUser.toString());
            CACHE_CONTAINER.put(CACHE_USER_PREFIX + newUser.getId(), newUser);

        }
        return updateSuccess;
    }

    /**
     * @description: 删除缓存
     * @param: joinPoint
     * @return: java.lang.Object
     * @author: Dawn
     * @date: 2020/3/3 19:53
     */
    @Around(value = POINTCUT_USER_REMOVE)
    public Object cacheUserRemove(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean removeSuccess = (Boolean) joinPoint.proceed();
        if (removeSuccess) {
            // 数据库删除成功，移除缓存中的数据
            log.info("用户缓存已经删除：" + CACHE_USER_PREFIX + id);
            CACHE_CONTAINER.remove(CACHE_USER_PREFIX + id);
        }
        return removeSuccess;
    }
}
