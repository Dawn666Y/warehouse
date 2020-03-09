<!-- TOC -->

- [仓库管理系统的搭建](#仓库管理系统的搭建)
  - [1. 项目前台模版的修改](#1-项目前台模版的修改)
    - [1.1. 替换 layuicms 中的 layui 版本](#11-替换-layuicms-中的-layui-版本)
    - [1.2. 查看页面是否导入成功](#12-查看页面是否导入成功)
    - [1.3. 删除 index 不需要的内容](#13-删除-index-不需要的内容)
    - [1.4. 修改工作台 page/main.html](#14-修改工作台-pagemainhtml)
  - [2. 搭建 Spring Boot 项目](#2-搭建-spring-boot-项目)
    - [2.1. 引入依赖](#21-引入依赖)
    - [2.2. yaml 配置](#22-yaml-配置)
    - [2.3. 实现登录功能](#23-实现登录功能)
      - [2.3.1. 代码生成](#231-代码生成)
      - [2.3.2. 创建 ActiveUser 类](#232-创建-activeuser-类)
      - [2.3.3. 创建 UserRealm](#233-创建-userrealm)
      - [2.3.4. 配置 shiro](#234-配置-shiro)
      - [2.3.5. 编写 LoginController](#235-编写-logincontroller)
      - [2.3.6. 修改前台页面代码](#236-修改前台页面代码)
    - [2.4. 生成菜单](#24-生成菜单)
      - [2.4.1. 代码生成](#241-代码生成)
      - [2.4.2. 建立 DataGridView 类](#242-建立-datagridview-类)
      - [2.4.3. 处理数据转换成 json](#243-处理数据转换成-json)
      - [2.4.4. 创建 MenuController](#244-创建-menucontroller)
      - [2.4.5. 修改前台页面](#245-修改前台页面)
    - [2.5. 修改登录后工作台](#25-修改登录后工作台)
    - [2.6. 日志管理系统](#26-日志管理系统)
      - [2.6.1. 生成代码](#261-生成代码)
      - [2.6.2. 编写 LogLoginController](#262-编写-loglogincontroller)
      - [2.6.3. 前台页面和 js](#263-前台页面和-js)
      - [2.6.4. 其它修改](#264-其它修改)
      - [2.6.5. 问题总结](#265-问题总结)
    - [2.7. 公告管理系统](#27-公告管理系统)
    - [2.8. 部门管理系统](#28-部门管理系统)
    - [2.9. 缓存切面](#29-缓存切面)
    - [2.10. 菜单管理系统](#210-菜单管理系统)
    - [2.11. 权限管理系统](#211-权限管理系统)
    - [2.12. 角色管理系统](#212-角色管理系统)
    - [2.13. 用户管理系统](#213-用户管理系统)

<!-- /TOC -->

本文为项目搭建日志

# 仓库管理系统的搭建

> 项目来自哔哩哔哩雷哥的免费教学视频： [【springboot+layui+mybatisplus+shiro】仓库管理系统【雷哥】](https://www.bilibili.com/video/av69172830)，我的项目源码：[warehouse](https://github.com/Dawn666Y/warehouse)，请在阅读文档搭建项目的过程中结合源码来看

## 1. 项目前台模版的修改

> 使用 **layui** 框架来搭建页面，**layui** 是当下流行的对后端工程师友好的前端页面框架，具有==开箱即用==的特点，能够帮助后台开发者快速的搭建一个完善的页面。<br/><br/>本项目将使用开源项目 layuicms2.0 来完成页面布局<br/>github 地址：[layuicms2.0](https://github.com/BrotherMa/layuicms2.0)

### 1.1. 替换 layuicms 中的 layui 版本

layuicms2.0 默认使用 layui 版本为 ~~v2.2.5~~ ，我们将其更新为最新的 v2.5.6 ，要注意 layui/layui.js 和 layui/css/layui.css 中包含一些 layuicms 自带的内容，替换后要添加上。

- layui.js：

```js
//防止页面单独打开【登录页面除外】
if (
  /layuicms2.0\/page/.test(top.location.href) &&
  !/login.html/.test(top.location.href)
) {
  top.window.location.href =
    window.location.href.split("layuicms2.0/page/")[0] + "layuicms2.0/";
}
//外部图标链接
var iconUrl = "https://at.alicdn.com/t/font_400842_q6tk84n9ywvu0udi.css";
```

- layui.css：

```css
/* 自添加样式*/
@import "https://at.alicdn.com/t/font_400842_q6tk84n9ywvu0udi.css";
.layui-icon {
  font-size: 16px !important;
}
.mag0 {
  margin: 0 !important;
}
::selection {
  background: #ff5722;
  color: #fff;
}
.layui-red {
  color: #f00 !important;
  font-weight: bold;
}
.layui-blue {
  color: #01aaed !important;
}
```

> 注意：作者在界面中均添加了版权开源声明公告，该内容代码位于 js/cache.js ，我们为了防止它弹出选择注掉这部分内容，删掉也可以，函数名称为 showNotice()

- cache.js

```js
//公告层
function showNotice() {
  // 这是layuicms作者添加的模板公告内容，咱们把它注掉
  // layer.open({
  //     type: 1,
  //     title: "系统公告",
  //     area: '300px',
  //     shade: 0.8,
  //     id: 'LAY_layuipro',
  //     btn: ['火速围观'],
  //     moveType: 1,
  //     content: '<div style="padding:15px 20px; text-align:justify; line-height: 22px; text-indent:2em;border-bottom:1px solid #e2e2e2;"><p class="layui-red">请使用模版前请务必仔细阅读首页右下角的《更新日志》，避免使用中遇到一些简单的问题造成困扰。</p></pclass></p><p>1.0发布以后发现很多朋友将代码上传到各种素材网站，当然这样帮我宣传我谢谢大家，但是有部分朋友上传到素材网站后将下载分值设置的相对较高，需要朋友们充钱才能下载。本人发现后通过和站长、网站管理员联系以后将分值调整为不需要充值才能下载或者直接免费下载。在此郑重提示各位：<span class="layui-red">本模版已进行作品版权证明，不管以何种形式获取的源码，请勿进行出售或者上传到任何素材网站，否则将追究相应的责任。</span></p></div>',
  //     success: function(layero){
  //         var btn = layero.find('.layui-layer-btn');
  //         btn.css('text-align', 'center');
  //         btn.on("click",function(){
  //             tipsShow();
  //         });
  //     },
  //     cancel: function(index, layero){
  //         tipsShow();
  //     }
  // });
}
```

今后为了方便我将 layui 版本升级后的 layuicms2.0 单独保存以便重复使用。

---

### 1.2. 查看页面是否导入成功

> 小知识：Spring Boot 项目初始化的 **resources/static** 文件夹可以自由访问无需添加 controller 层配置路径，所以在搭建项目修改页面的初期，建议将全部内容都放在 static 文件夹下，浏览器访问路径就是 static 下的文件路径(不包含~~static~~)，这样可以第一时间查看页面修改的样式

登录页面：

<div align="center">
<img src="https://i.loli.net/2020/02/24/UjZKyFS51msoMAw.png"/>
</div>

登录页面的背景图片可以换，目录是 **images/login_bg.jpg**

系统主页：

<div align="center">
<img src="https://i.loli.net/2020/02/24/XeMJCYOof2BAmy7.png"/>
</div>

---

### 1.3. 删除 index 不需要的内容

首先删除左上角的下拉框中无用的功能，只保留个人资料修改密码和退出

<div align="center">
<img src="https://i.loli.net/2020/02/24/Of3jz5FVkbB7Xvi.png"/>
</div>
<div align="center">
<img src="https://i.loli.net/2020/02/24/B2M7j43HzsZulO6.png"/>
</div>
修改页脚

<div align="center">
<img src="https://i.loli.net/2020/02/24/SsPonVzGqfguaAc.png"/>
</div>
<div align="center">
<img src="https://i.loli.net/2020/02/24/HmbGnkTWiBQgaeo.png"/>
</div>
<div align="center">
<img src="https://i.loli.net/2020/02/24/TvbADq1BwxuWONf.png"/>
</div>

修改上方菜单

> layuicms2.0 中上方菜单对应着左侧的菜单栏，点选上方菜单将会切换左侧菜单栏，我们需要首先修改 json/navs.json，这里是菜单内容的数据来源，删除层次只保留一个 json 菜单串，然后再修改 js/index.js 中的内容，不需要再判断，最后删除 index 页面上方菜单的标签

navs.json：

```json
// {
//   "contentManagement":
[
  {
    "title": "文章列表",
    "icon": "icon-text",
    "href": "page/news/newsList.html",
    "spread": false
  },
  {
    "title": "图片管理",
    "icon": "&#xe634;",
    "href": "page/img/images.html",
    "spread": false
  },
  {
    "title": "其他页面",
    "icon": "&#xe630;",
    "href": "",
    "spread": false,
    "children": [
      {
        "title": "404页面",
        "icon": "&#xe61c;",
        "href": "page/404.html",
        "spread": false
      },
      {
        "title": "登录",
        "icon": "&#xe609;",
        "href": "page/login/login.html",
        "spread": false,
        "target": "_blank"
      }
    ]
  }
]
//   ,
//   "memberCenter": [
//     {
//       "title": "用户中心",
//       "icon": "&#xe612;",
//       "href": "page/user/userList.html",
//       "spread": false
//     },
//     {
//       "title": "会员等级",
//       "icon": "icon-vip",
//       "href": "page/user/userGrade.html",
//       "spread": false
//     }
//   ],
//   "systemeSttings": [
//     {
//       "title": "系统基本参数",
//       "icon": "&#xe631;",
//       "href": "page/systemSetting/basicParameter.html",
//       "spread": false
//     },
//     {
//       "title": "系统日志",
//       "icon": "icon-log",
//       "href": "page/systemSetting/logs.html",
//       "spread": false
//     },
//     {
//       "title": "友情链接",
//       "icon": "&#xe64c;",
//       "href": "page/systemSetting/linkList.html",
//       "spread": false
//     },
//     {
//       "title": "图标管理",
//       "icon": "&#xe857;",
//       "href": "page/systemSetting/icons.html",
//       "spread": false
//     }
//   ],
//   "seraphApi": [
//     {
//       "title": "三级联动模块",
//       "icon": "icon-mokuai",
//       "href": "page/doc/addressDoc.html",
//       "spread": false
//     },
//     {
//       "title": "bodyTab模块",
//       "icon": "icon-mokuai",
//       "href": "page/doc/bodyTabDoc.html",
//       "spread": false
//     },
//     {
//       "title": "三级菜单",
//       "icon": "icon-mokuai",
//       "href": "page/doc/navDoc.html",
//       "spread": false
//     }
//   ]
// }
```

index.js：

```js
function getData(json) {
  $.getJSON(tab.tabConfig.url, function(data) {
    // if (json == "contentManagement") {
    dataStr = data.contentManagement;
    //重新渲染左侧菜单
    tab.render();
    // } else if (json == "memberCenter") {
    //   dataStr = data.memberCenter;
    //   //重新渲染左侧菜单
    //   tab.render();
    // } else if (json == "systemeSttings") {
    //   dataStr = data.systemeSttings;
    //   //重新渲染左侧菜单
    //   tab.render();
    // } else if (json == "seraphApi") {
    //   dataStr = data.seraphApi;
    //   //重新渲染左侧菜单
    //   tab.render();
    // }
  });
}
```

---

### 1.4. 修改工作台 page/main.html

只保留最新消息的表格，其余全部删除

- 删除后：

<div align="center">
<img src="https://i.loli.net/2020/02/24/TvbADq1BwxuWONf.png"/>
</div>

---

## 2. 搭建 Spring Boot 项目

### 2.1. 引入依赖

```xml
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.3.1</version>
        </dependency>

        <!-- commons-lang3工具包 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.9</version>
        </dependency>

        <!-- 集成阿里巴巴fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.60</version>
        </dependency>

        <!-- spring整合shiro -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.4.2</version>
        </dependency>
        <!-- thymeleaf对shiro的支持 -->
        <dependency>
            <groupId>com.github.theborakompanioni</groupId>
            <artifactId>thymeleaf-extras-shiro</artifactId>
            <version>2.0.0</version>
        </dependency>

        <!-- log4j -->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <!-- druid-spring-boot-starter阿里数据库连接池 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.21</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

---

### 2.2. yaml 配置

```yaml
server:
  port: 9000
spring:
  application:
    name: warehouse
  datasource:
    # 连接池相关配置
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/shining?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8
      username: root
      password: YLM19980514i
      max-active: 20
      max-wait: 5000
      initial-size: 1
      filters: log4j,stat,wall
      validation-query: SELECT 'x' # 验证连接
      enable: true
      # 监控配置
      stat-view-servlet:
        enabled: true
        login-username: root
        login-password: root
        url-pattern: /druid/*
  # json处理配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

  # 配置thymeleaf
  thymeleaf:
    cache: false
    enabled: true
  # 配置devtools
  devtools:
    restart:
      enabled: true
      additional-paths:
        - src/main/java
        - src/main/resources
# 开启Mybatis的驼峰命名法
mybatis-plus:
  mapper-locations:
    - classpath:mapper/*/*Mapper.xml
  global-config:
    db-config:
      id-type: auto
    banner: true
  configuration:
    map-underscore-to-camel-case: true
```

---

### 2.3. 实现登录功能

#### 2.3.1. 代码生成

利用 mybatis-plus 提供的代码生成器生成数据库 user 表的代码，包括 entity、mapper、service、controller。

<div align=center><img src="https://i.loli.net/2020/02/24/OUTSZWyxGwNPjJe.png" /></div>

在启动类上添加`@mapperscan`注解

#### 2.3.2. 创建 ActiveUser 类

```java
package com.warehouse.sys.common;

import com.warehouse.sys.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: mybaitis-plus-generator
 * @description: 登录的用户
 * @author: Dawn
 * @create: 2020-02-24 18:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveUser {

    // 登录用户
    private User user;

    // 角色
    private List<String> roles;

    // 权限
    private List<String> permissions;

}

```

#### 2.3.3. 创建 UserRealm

```java
package com.warehouse.sys.config.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.sys.common.entity.ActiveUser;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.IUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: mybaitis-plus-generator
 * @description: userRealm
 * @author: Dawn
 * @create: 2020-02-24 19:01
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private IUserService userService;

    /**
     * 返回类名以供判断
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 授权方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 认证方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname",authenticationToken.getPrincipal().toString());

        User user = userService.getOne(queryWrapper);
        if (null!=user){
            // 填充一个ActiveUser
            ActiveUser activeUser = ActiveUser.builder()
                    .user(user)
                    .build();
            // 取出该user的盐值
            ByteSource bytesSalt = ByteSource.Util.bytes(user.getSalt());

            // 返回
            return new SimpleAuthenticationInfo(activeUser,
                    user.getPwd(),
                    bytesSalt,
                    this.getName());
        }
        return null;
    }
}
```

#### 2.3.4. 配置 shiro

创建`ShiroConfig`类

```java

package com.warehouse.sys.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.warehouse.sys.config.realm.UserRealm;
import lombok.Data;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;

/**
 * @program: mybaitis-plus-generator
 * @description: shiro配置类
 * @author: Dawn
 * @create: 2020-02-24 19:14
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(value = { SecurityManager.class })
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroConfig {

    private static final String SHIRO_DIALECT = "shiroDialect";
    private static final String SHIRO_FILTER = "shiroFilter";
    private String hashAlgorithmName = "md5";// 加密方式
    private int hashIterations = 2;// 散列次数
    private String loginUrl = "/index.html";// 默认的登陆页面

    private String[] anonUrls;
    private String logOutUrl;
    private String[] authcUlrs;

    /**
     * 声明凭证匹配器
     */
    @Bean("credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(hashAlgorithmName);
        credentialsMatcher.setHashIterations(hashIterations);
        return credentialsMatcher;
    }

    /**
     * 声明userRealm
     */
    @Bean("userRealm")
    public UserRealm userRealm(CredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        // 注入凭证匹配器
        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }

    /**
     * 配置SecurityManager
     */
    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入userRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 配置shiro的过滤器
     */
    @Bean(SHIRO_FILTER)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        // 设置未登陆的时要跳转的页面
        factoryBean.setLoginUrl(loginUrl);
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        // 设置放行的路径
        if (anonUrls != null && anonUrls.length > 0) {
            for (String anon : anonUrls) {
                filterChainDefinitionMap.put(anon, "anon");
            }
        }
        // 设置登出的路径
        if (null != logOutUrl) {
            filterChainDefinitionMap.put(logOutUrl, "logout");
        }
        // 设置拦截的路径
        if (authcUlrs != null && authcUlrs.length > 0) {
            for (String authc : authcUlrs) {
                filterChainDefinitionMap.put(authc, "authc");
            }
        }
        Map<String, Filter> filters=new HashMap<>();
//		filters.put("authc", new ShiroLoginFilter());
        //配置过滤器
        factoryBean.setFilters(filters);
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }

    /**
     * 注册shiro的委托过滤器，相当于之前在web.xml里面配置的
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<DelegatingFilterProxy>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName(SHIRO_FILTER);
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /* 加入注解的使用，不加入这个注解不生效--开始 */
    /**
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    /* 加入注解的使用，不加入这个注解不生效--结束 */

    /**
     * 这里是为了能在html页面引用shiro标签，上面两个函数必须添加，不然会报错
     *
     * @return
     */
    @Bean(name = SHIRO_DIALECT)
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
```

在 yaml 中添加 shiro 的配置

```yaml
#shiro的配置
shiro:
  hash-algorithm-name: md5
  hash-iterations: 2
  anon-urls:
    - /index.html*
    - /sys/toLogin*
    - /login/login*
    - /resources/**
  login-url: /index.html
  log-out-url: /login/logout*
  authc-ulrs:
    - /**
```

#### 2.3.5. 编写 LoginController

`LoginController`

```java
package com.warehouse.sys.controller;

import com.warehouse.sys.common.utils.WebUtils;
import com.warehouse.sys.common.entity.ActiveUser;
import com.warehouse.sys.common.entity.ResultObj;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Dawn
 * @since 2020-02-24
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping("/login")
    public ResultObj login(String loginname, String pwd) {

        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken usernamePasswordToken = new UsernamePasswordToken(loginname, pwd);
        try {
            // shiro登录
            subject.login(usernamePasswordToken);
            // 获取登录的账户
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            // 自建工具类获取session
            HttpSession session = WebUtils.getSession();
            // 添加登录信息到session中
            session.setAttribute("user", activeUser);

            // 自定义的返回数据格式
            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR;
        }
    }

}
```

`ResultObj`

```java
package com.warehouse.sys.common.entity;

import com.warehouse.sys.common.constants.WebConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: mybaitis-plus-generator
 * @description:
 * @author: Dawn
 * @create: 2020-02-25 16:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    public static final ResultObj LOGIN_SUCCESS=
            new ResultObj(WebConstants.OK,"登录成功！");

    public static final ResultObj LOGIN_ERROR=
            new ResultObj(WebConstants.ERROR,"登录失败！请检查用户名和密码！");

    private Integer code;
    private String msg;
}

```

`WebConstants`

```java
package com.warehouse.sys.common.constants;

/**
 * @program: mybaitis-plus-generator
 * @description: 一些web层面的常量
 * @author: Dawn
 * @create: 2020-02-24 19:31
 */
public interface WebConstants {
    Integer OK = 200;
    Integer ERROR = -1;
}
```

`WebUtils`

```java

package com.warehouse.sys.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @program: mybaitis-plus-generator
 * @description: web工具类
 * @author: Dawn
 * @create: 2020-02-25 17:09
 */
public class WebUtils {

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }
}
```

#### 2.3.6. 修改前台页面代码

`page/login/login.js`

```js
//登录按钮
form.on("submit(login)", function(data) {
  // $(this)
  //   .text("登录中...")
  //   .attr("disabled", "disabled")
  //   .addClass("layui-disabled");
  // setTimeout(function() {
  //   window.location.href = "/layuicms2.0";
  // }, 1000);
  // return false;

  // layer.msg(data.field);
  var btn = $(this);
  // 设置登录按钮为不可点击
  btn
    .text("登录中...")
    .attr("disable", "disable")
    .addClass("layui-disabled");
  $.post("/login/login", data.field, function(result) {
    btn
      .text("登录")
      .attr("disable", false)
      .removeClass("layui-disabled");

    if (result.code == 200) {
      // 跳转到主页
      layer.msg(result.msg);
      location.href = "/sys/index";
    } else {
      layer.msg(result.msg);
    }
  });
  return false;
});
```

为 login.html 的表单添加与后台函数对应的 name 属性

> 启动项目测试登录时发现样式全无，检查后发现，雷哥的项目把静态资源全部放在 static 的自建 resources 目录下，配置 shiro 的时候只需添加静态目录`/resources/**`即可过滤，而我的静态资源是直接放在 static 文件夹中的，需要**为 shiro 手动添加所有的静态资源目录，也可以像雷哥一样放在一个公共目录下**

添加之后的 yaml 的 shiro 配置如下:

```yaml
#shiro的配置
shiro:
  hash-algorithm-name: md5
  hash-iterations: 2
  anon-urls:
    - /index.html*
    - /sys/toLogin*
    - /login/login*
    # 静态资源不拦截
    - /resources/**
  login-url: /index.html
  log-out-url: /login/logout*
  authc-ulrs:
    - /**
```

测试登录成功

> 主页左侧的菜单树又出现故障，这里需要修改 resources/js/index.js，把里面的所有请求路径统一修改成包含`/resources/**`的样子，类似如下

```js
layui
  .config({
    base: "/resources/js/"
  })
  .extend({
    bodyTab: "bodyTab"
  });
```

> shiro 检测到未登录会请求`/index.html`，我们在 static 目录里添加 index.html，设置成跳转到登录页面，代码如下

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>shiro跳转</title>
  </head>
  <body>
    <script type="text/javascript">
      window.location.href = "/sys/toLogin";
    </script>
  </body>
</html>
```

接下来修改前台页面中下面几处，让它显示成用户的信息

<div align=center><img src="https://i.loli.net/2020/02/25/529zgXPRkeV8CYE.png" /></div>

在页面中使用模版引擎 thymeleaf ，首先要在页面上引入 thymeleaf 命名空间`<html xmlns:th="http://www.thymeleaf.org">`<br>
再按照`th:text="${session.user.name!=null}?${session.user.name}"`的格式重写这几处标签

---

### 2.4. 生成菜单

> layuicms2.0 的菜单是从 json 串中解析得到的，那么我们就要根据登录用户的身份取判断应当加载那些菜单，这里有几个步骤

1. 根据登录用户的身份去后台查询他拥有的角色
2. 根据角色查询权限
3. 根据权限查询菜单
4. 根据菜单构造 json 字符串返回给前台解析

- json 串的构造

| 字段     | 含义                              |
| :------- | :-------------------------------- |
| title    | 菜单标题                          |
| icon     | 菜单图标                          |
| href     | 菜单链接                          |
| spread   | 是否展开 _true 或 false_          |
| children | 菜单子节点 _内部又是一个菜单数据_ |

- 示例：

```json
[
  {
    "title": "其他页面",
    "icon": "&#xe630;",
    "href": "",
    "spread": false,
    "children": [
      {
        "title": "404页面",
        "icon": "&#xe61c;",
        "href": "page/404.html",
        "spread": false
      },
      {
        "title": "登录",
        "icon": "&#xe609;",
        "href": "page/login/login.html",
        "spread": false,
        "target": "_blank"
      }
    ]
  }
]
```

#### 2.4.1. 代码生成

模块名：sys

表名：sys_permission

过程略，和上边一样的

#### 2.4.2. 建立 DataGridView 类

这个类是后端向前端传递 json 数据的包装类，除了 json 数据外包含一些信息

`DataGridView`

```java
package com.warehouse.sys.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: warehouse
 * @description: json数据实体
 * @author: Dawn
 * @create: 2020-02-25 21:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataGridView {

    // 执行是否成功的代码
    private Integer code=0;
    // 传递的消息
    private String msg="";
    // 数据的长度
    private Long count=0L;
    // json数据
    private Object data;

    public DataGridView(Long count, Object data) {
        super();
        this.count = count;
        this.data = data;
    }

    public DataGridView(Object data) {
        super();
        this.data = data;
    }
}

```

#### 2.4.3. 处理数据转换成 json

> 数据库中的菜单数据所在的表 sys_permission 还存有权限数据，也就是说传递到前端的数据格式并不是 layuicms2.0 的标准 json 格式，还存在一些没有数据的空字段，我们需要在向前端返回数据之前将菜单数据处理成前端的标准格式

- 创建 TreeNode 类

```java
package com.warehouse.sys.common.entity;

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
    private Integer pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private List<TreeNode> children;
}

```

- 创建 TreeBuilder 类-构建者模式

```java
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

```

#### 2.4.4. 创建 MenuController

```java
package com.warehouse.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.warehouse.sys.common.constants.DataSourcesConstants;
import com.warehouse.sys.common.constants.WebConstants;
import com.warehouse.sys.common.entity.DataGridView;
import com.warehouse.sys.common.entity.TreeNode;
import com.warehouse.sys.common.utils.TreeNodeBuilder;
import com.warehouse.sys.common.utils.WebUtils;
import com.warehouse.sys.entity.Permission;
import com.warehouse.sys.entity.User;
import com.warehouse.sys.service.IPermissionService;
import com.warehouse.sys.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: warehouse
 * @description: 菜单控制器
 * @author: Dawn
 * @create: 2020-02-25 21:04
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping("/loadLeftMenuJson")
    public DataGridView loadLeftMenuJson(PermissionVo permissionVo) {

        // 获取当前用户的信息
        User user = (User) WebUtils.getSession()
                .getAttribute(WebConstants.USER_ATTRIBUTE);

        // 构建查询语句
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();

        // sys_permission表中包含权限和菜单，这里只查询菜单数据
        wrapper.eq("type", DataSourcesConstants.SYS_PERMISSION_TYPE_MENU);
        // 数据可用状态
        wrapper.eq("available", DataSourcesConstants.AVAILABLE_TRUE);

        // 查询
        List<Permission> menu = null;
        // 判断用户类型
        if (DataSourcesConstants.USER_TYPE_SUPER.equals(user.getType()))
            // 是超级管理员则直接查询
            menu = permissionService.list(wrapper);
        else {
            // 不是超级管理员则按照用户-角色-权限查询
            // 代码待完善
            permissionService.list(wrapper);
        }
        // 把permission数据转成有层次结构的菜单
        List<TreeNode> finalMenu = new ArrayList<>();
        if (menu != null && menu.size() > 0) {
            // 初始化菜单节点
            List<TreeNode> nodes = new ArrayList<>();
            menu.forEach(i ->
                    // 此处使用了lombok提供的builder初始化方法
                    nodes.add(TreeNode.builder()
                            .id(i.getId())
                            .pid(i.getPid())
                            .title(i.getTitle())
                            .icon(i.getIcon())
                            .href(i.getHref())
                            // 菜单是否展开
                            .spread(i.getOpen().equals(DataSourcesConstants.MENU_OPEN))
                            .children(new ArrayList<>())
                            .build())
            );
            // 排列菜单层次
            finalMenu = TreeNodeBuilder.build(nodes, 1);
        }
        return new DataGridView(finalMenu);
    }
}

```

#### 2.4.5. 修改前台页面

- 改变前台请求菜单数据的 url

```js
url: "/menu/loadLeftMenuJson"; //获取菜单json地址
```

- 由于我们封装了菜单数据，取值的对象要更改一下

```js
dataStr = data.data;
```

测试成功：

<div align=center><img src="https://i.loli.net/2020/02/26/EmOrk1Cs9FQ3tdR.png" /></div>

---

### 2.5. 修改登录后工作台

由于 index 页面是通过 iframe 请求工作台 main 页面，工作台上方有一个欢迎栏需要用户姓名的数据，于是尝试引入 thymeleaf 标签，把工作台页面`page/main.html`转移到项目的 templates 下(修改页面里所有的静态资源引用路径--后面不再赘述这一步骤)，再修改 index 页面的工作台请求路径，在`SystemController`中添加路径

```java
    /**
     * 跳转到工作台
     * */
    @RequestMapping("/toDesktop")
    public String toDesktop(){
        return "system/main/main";
    }
```

在 main 页面引用的 js 文件中添加

```js
//获取登录用户名
var loginUserName = "[[${session.user.name}]]";
```

来获取登录用户的姓名，失败

![image.png](https://i.loli.net/2020/02/28/9gof6ER7aJBbcIK.png)

然后我意识到 js 是在浏览器端运行的代码，**session 是保存在服务器中的**所以不能这样引用，解决方案是把工作台引用的`main.js`中的 js 代码直接复制到页面中，这样模版引擎才能够生效

成功

![image.png](https://i.loli.net/2020/02/28/nqfrwKx5VRshdCU.png)

---

### 2.6. 日志管理系统

日志信息的增删查改

#### 2.6.1. 生成代码

同上

#### 2.6.2. 编写 LogLoginController

```java
    /**
     * @description: 查询登录信息
     * @param: logLoginVo
     * @return: com.warehouse.sys.common.entity.DataGridView
     * @author: Dawn
     * @date: 2020/2/28 18:08
     */
    @RequestMapping("/loadAllLog")
    public DataGridView loadAllLog(LogLoginVo logLoginVo) {

        Page<LogLogin> logLoginPage = new Page<>(logLoginVo.getPage(), logLoginVo.getLimit());
        QueryWrapper<LogLogin> wrapper = new QueryWrapper<>();

        // 添加查询条件，按照登录名查询
        wrapper.like(StringUtils.isNotBlank(logLoginVo.getLoginname()),
                "loginname", logLoginVo.getLoginname());
        // 按照登录IP查询
        wrapper.like(StringUtils.isNotBlank(logLoginVo.getLoginip()),
                "loginip", logLoginVo.getLoginip());
        // 按照登录时间范围查询
        wrapper.ge(logLoginVo.getStartTime() != null,
                "logintime", logLoginVo.getStartTime());
        wrapper.le(logLoginVo.getEndTime() != null,
                "logintime", logLoginVo.getEndTime());
        // 按照登录时间排序，新登陆的在前面
        wrapper.orderByDesc("logintime");

        logLoginService.page(logLoginPage, wrapper);

        return new DataGridView(logLoginPage.getTotal(), logLoginPage.getRecords());
    }

    /**
     * @description: 按照id删除登陆日志信息
     * @param: id
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/2/29 13:26
     */
    @RequestMapping("/deleteLogLogin")
    public ResultObj deleteLogLogin(Integer id) {
        try {
            boolean removeSuccess = logLoginService.removeById(id);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * @description: 批量删除
     * @param: loginVo
     * @return: com.warehouse.sys.common.entity.ResultObj
     * @author: Dawn
     * @date: 2020/2/29 13:44
     */
    @RequestMapping("/batchDeleteLog")
    public ResultObj batchDeleteLog(LogLoginVo loginVo) {
        try {
            ArrayList<Integer> logIds = new ArrayList<>();
            Collections.addAll(logIds, loginVo.getIds());
            System.out.println("ids = " + logIds.toString());
            boolean removeSuccess = logLoginService.removeByIds(logIds);
            if (removeSuccess)
                return ResultObj.DELETE_SUCCESS;
            else
                throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
```

#### 2.6.3. 前台页面和 js

新建 templates/system/main/logManager.html

```html
<body class="childrenBody">
  <!-- 查询条件开始 -->

  <div lay-filter="searchForm">
    <blockquote class="layui-elem-quote">
      <form action="" method="POST" id="searchForm" class="layui-form">
        <div class="layui-form-item">
          <div class="layui-inline">
            <label class="layui-form-label" style="width: 80px;">登录名</label>
            <div class="layui-input-inline" style="width: 100px;">
              <input
                type="text"
                id="loginname"
                name="loginname"
                autocomplete="off"
                class="layui-input"
              />
            </div>
          </div>
          <div class="layui-inline">
            <label class="layui-form-label" style="width: 80px;">登录IP</label>
            <div class="layui-input-inline" style="width: 100px;">
              <input
                type="text"
                id="loginip"
                name="loginip"
                autocomplete="off"
                class="layui-input"
              />
            </div>
          </div>
          <div class="layui-inline">
            <label class="layui-form-label">开始时间</label>
            <div class="layui-input-inline">
              <input
                type="text"
                id="startTime"
                name="startTime"
                lay-verify="datetime"
                readonly="readonly"
                placeholder="年-月-日 时:分:秒"
                autocomplete="off"
                class="layui-input"
              />
            </div>
          </div>
          <div class="layui-inline">
            <label class="layui-form-label">结束时间</label>
            <div class="layui-input-inline">
              <input
                type="text"
                id="endTime"
                name="endTime"
                lay-verify="datetime"
                readonly="readonly"
                placeholder="年-月-日 时:分:秒"
                autocomplete="off"
                class="layui-input"
              />
            </div>
          </div>
          <div class="layui-form-item">
            <div class="layui-input-block" style="text-align:center">
              <button
                type="button"
                class="layui-btn"
                lay-submit=""
                lay-filter="doSearch"
              >
                <span class="layui-icon layui-icon-search"></span>查询
              </button>
              <button
                id="resetBtn"
                type="reset"
                class="layui-btn layui-btn-warm"
              >
                <span class="layui-icon layui-icon-refresh-1"></span>重置
              </button>
            </div>
          </div>
        </div>
      </form>
    </blockquote>
  </div>
  <!-- 查询条件结束 -->

  <!-- 数据表格开始 -->
  <div>
    <table
      class="layui-hide"
      id="logInfoTable"
      lay-filter="logInfoTable"
    ></table>

    <div id="logInfoToolBar" style="display: none;">
      <button
        type="button"
        lay-event="batchDelete"
        class="layui-btn layui-btn-sm layui-btn-danger"
      >
        <span class="layui-icon layui-icon-delete"></span>批量删除
      </button>
    </div>

    <div id="logInfoRowBar" style="display: none;">
      <button
        type="button"
        lay-event="delete"
        class="layui-btn layui-btn-sm layui-btn-danger"
      >
        <span class="layui-icon layui-icon-delete"></span>删除
      </button>
    </div>
  </div>
  <!-- 数据表格结束 -->
  <script type="text/javascript" src="/resources/layui/layui.js"></script>
  <script type="text/javascript" src="/resources/js/logManager.js"></script>
</body>
```

新建 resources/js/logManager.js

```js
layui.use(["table", "laydate", "jquery", "form", "layer"], function() {
  var table = layui.table;
  var laydate = layui.laydate;
  var $ = layui.jquery;
  var form = layui.form;
  var layer = layui.layer;

  // 控件渲染开始日期
  laydate.render({
    elem: "#startTime",
    type: "datetime"
  });

  // 控件渲染结束日期
  laydate.render({
    elem: "#endTime",
    type: "datetime"
  });

  var tableIns = table.render({
    elem: "#logInfoTable",
    url: "/log-login/loadAllLog",
    toolbar: "#logInfoToolBar", //开启头部工具栏，并为其绑定左侧模板
    title: "登录日志表",
    height: "full-160",
    page: true,
    cols: [
      [
        { type: "checkbox", fixed: "left" },
        { field: "id", title: "id", width: 80 },
        { field: "loginname", title: "登陆名称", width: 200 },
        { field: "loginip", title: "登录IP" },
        { field: "logintime", title: "登录时间" },
        { fixed: "right", title: "操作", toolbar: "#logInfoRowBar", width: 150 }
      ]
    ]
  });

  // 按条件搜索查看
  form.on("submit(doSearch)", function(data) {
    tableIns.reload({
      where: data.field,
      page: { curr: 1 }
    });
    return false;
  });

  // 监听表格工具栏
  table.on("toolbar(logInfoTable)", function(obj) {
    switch (obj.event) {
      case "batchDelete":
        batchDelete();
        break;
    }
  });

  // 监听行工具栏
  table.on("tool(logInfoTable)", function(obj) {
    var data = obj.data; //获得当前行数据
    switch (obj.event) {
      case "delete":
        deleteRow(data);
        break;
    }
  });

  // 批量删除函数
  function batchDelete() {
    // 获取选中行
    var checkStatus = table.checkStatus("logInfoTable");
    // 获取选中行的数据
    var data = checkStatus.data;
    // 获取选中多少行
    var length = data.length;
    if (length > 0) {
      // 选中了内容
      // 删除操作确认
      layer.confirm(
        "你确定要删除这些数据吗？该操作不可逆！",
        { icon: 7, title: "警告！" },
        function(index) {
          // 开始封装全部id
          var ids = "";
          $.each(data, function(index, item) {
            if (index == 0)
              // 第一条数据拼接
              ids += "ids=" + item.id;
            // 剩余数据拼接
            else ids += "&ids=" + item.id;
          });
          // 测试能否获取数据
          // layer.msg(ids);
          // alert(ids);

          // ajax发送批量删除请求
          $.post("/log-login/batchDeleteLog", ids, function(result) {
            if (result.code == 200) {
              // 删除成功，重新加载表格
              tableIns.reload();
            }
            // 无论删除是否成功都显示信息msg
            layer.msg(result.msg);
          });
          layer.close(index);
        }
      );
    } else {
      // 没有选中内容
      layer.msg("请先选中行");
    }
  }

  // 删除行函数
  function deleteRow(data) {
    layer.confirm(
      "确定删除这条数据吗？该操作不可逆！",
      { icon: 7, title: "警告！" },
      function(index) {
        // 发送单条删除请求
        $.post("/log-login/deleteLogLogin", { id: data.id }, function(result) {
          if (result.code == 200) {
            // 删除成功，重新加载表格
            tableIns.reload();
          }
          // 无论删除是否成功都显示信息msg
          layer.msg(result.msg);
        });
      }
    );
  }
});
```

#### 2.6.4. 其它修改

- 在 SystemController 中添加代码跳转

```java
    /**
     * 跳转到登录日志管理
     * */
    @RequestMapping("/toLogManager")
    public String toLogManager(){
        return "system/main/logManager";
    }
```

      测试后发现如果使用 localhost 访问网站，那么获取到的 IP 是 IPV6 格式的，用 127.0.0.1 访问网站，获取到的 IP 就是 127.0.0.1

- 修改数据库中的菜单链接为上面的请求路径

  我使用了 IDEA 自带的 Database 插件，发现修改不成功的现象，结果百度才了解到，提交数据表的修改要在修改行右键点击提交，或者 ctrl+enter 提交

- 在登录时为登陆日志表添加数据

  在 LoginController 的登录方法中添加代码

```java
// 填写登录日志
// 获取日志登录名
String logLoginname=activeUser.getUser().getName()+"-"+activeUser.getUser().getLoginname();
// 获取IP
String remoteHost = WebUtils.getRequest().getRemoteAddr();
LogLogin newLogLogin=LogLogin.builder()
        .loginname(logLoginname)
        .loginip(remoteHost)
        .logintime(new Date())
        .build();
// 保存登录日志
logLoginService.save(newLogLogin);
```

测试成功

![image.png](https://i.loli.net/2020/02/29/9noMeyUd4arkDIV.png)

#### 2.6.5. 问题总结

> 自己对 layui 或者应该说整个前端的内容都不算熟，应该花些功夫做做功课，在写 js 文件的时候就闹出很多次页面显示效果不能达到预期的问题，大多原因是对原生 JavaScript、jQuery、layui 的语法掌握不好，都是依靠 **查官方文档** 解决或者 **使用 IDEA 的自动排错** 发现，真的大大降低了开发的效率，应当引以为戒。

---

### 2.7. 公告管理系统

基本和上一个一样，该替换的替换，面向复制粘贴编程(狗头)，下面没有必要就不贴大量源码了

> 我们在这里添加一个 layui 的富文本编辑器用来书写公告，但是发现自带的表单提交方法`form.on("submit(doSubmit)", function(data) {}`不能同步富文本内容到表单，导致提交的公告没有内容，于是决定不使用 layui 的表单自带的提交方法，改为使用 jQuery 发送 ajax 请求提交表单，并为富文本添加一个全局变量，在打开添加和修改页面时为它初始化--这样初始化的目的是因为，**如果定义富文本编辑器全局变量时初始化，打开弹出层之后富文本编辑器的工具栏是不能使用的，所以要在打开弹出层之后再初始化富文本编辑器**，底层原因大概是和 layui 的模块加载顺序有关，我这个 JavaScript 菜鸡暂时做不出太清晰的解释，慢慢学吧

```js
// 为富文本定义全局变量
var contentText;
```

```js
// 初始化富文本编辑器
contentText = layedit.build("content");
```

```js
// 同步富文本和textarea里面的内容
layedit.sync(contentText);
```

最终效果：

![image.png](https://i.loli.net/2020/03/09/Q5VURwsItHTAc9F.png)

![image.png](https://i.loli.net/2020/03/09/7tSwQngsEdDeTCc.png)

![image.png](https://i.loli.net/2020/03/09/xT4dVbHJZzalSAI.png)

### 2.8. 部门管理系统

> 我们引入 layui 的拓展插件 dtree：[下载地址](https://fly.layui.com/extend/dtree/#download) 、[dtree 官网](https://fly.layui.com/extend/dtree/)、[dtree 组件文档](http://www.wisdomelon.com/DTreeHelper/)，组件的最终效果如图：

![image.png](https://i.loli.net/2020/03/04/7nygfNvLAzTDc4t.png)

我们解压缩下载的包，只需要里面的 layui_ext 这个文件夹，将其拷贝到项目的静态目录下即可

![image.png](https://i.loli.net/2020/03/04/Yg6wGUK1loNZEv2.png)

在页面中引用 dtree 模版除了引入 dtree 自己的样式文件以外，还应使用 extend 语句

```html
<!-- 引入dtree自己的css -->
<link
  rel="stylesheet"
  href="/resources/layui_ext/dtree/dtree.css"
  media="all"
/>
<link rel="stylesheet" href="/resources/layui_ext/dtree/font/dtreefont.css" />

<!-- body中存放树的容器 -->
<ul id="deptTree" class="dtree" data-id="0"></ul>
```

```js
layui
  .extend({
    // {/}的意思即代表采用自有路径，即不跟随 base 路径
    // 这里填写dtree.js文件所在的位置
    dtree: "/resources/layui_ext/dtree/dtree"
  })
  .use(["dtree"], function() {
    var dtree = layui.dtree;
  });
```

我们把部门管理页面分为左右两个部分，在 templates/sys/下建立一个 dept 文件夹存放包括 frame 容器在内的三个文件：deptManager.html、deptLeft,html、deptRight.html，以 Manager 页面做 frameset 的容器

```html
  <!-- 删除body标签，添加frameset -->
  <frameset cols="200,*" border="1" frameborder="yes"
    ><frame src="/sys/toDeptLeft" name="left">
    <frame src="/sys/toDeptRight" name="right">
  </frameset>
```

在后台添加指向 dept
Manager.html 左右两个 html 的路径

```java

    /**
     * 跳转到部门管理
     */
    @RequestMapping("/toDeptManager")
    public String toDeptManager() {
        return "system/dept/deptManager";
    }

    /**
     * 跳转到部门管理-left
     */
    @RequestMapping("/toDeptLeft")
    public String toDeptLeftManager() {
        return "system/dept/deptLeft";
    }

    /**
     * 跳转到部门管理-right
     */
    @RequestMapping("/toDeptRight")
    public String toDeptRightManager() {
        return "system/dept/deptRight";
    }
```

接下来编写具体的前台代码就好啦，具体看源码

> layui 的表单验证 `lay-verify=""` 只有在自带的表单提交函数触发时 `form.on("submit( any event )", function(data) {}` 才会执行，我们前面在公告管理中为了同步 textarea 标签富文本编辑器添加的 jQuery 表单提交方法会直接执行提交而跳过 layui 的验证

解决的方案：

- 如果没有同步某些标签内容的必要，请使用 layui 下的表单提交方法；
- 如果有同步的需要，自己写一个表单验证的 js 方法

最终效果：

![image.png](https://i.loli.net/2020/03/09/mvQqYg7dDOSWcPa.png)

点击左侧的树结构右边会弹出该部门对应的子部门

![image.png](https://i.loli.net/2020/03/09/i8W23E9VMc57oua.png)

点击删除之后弹出的提醒框

![image.png](https://i.loli.net/2020/03/09/zFDjLawqytpAHeW.png)

---

### 2.9. 缓存切面

在 sys 下新建包 cache，创建类 CacheAspect 做切面

```java
package com.warehouse.sys.cache;

import com.warehouse.sys.entity.Dept;
import com.warehouse.sys.vo.DeptVo;
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
public class CacheAspect {

    // 声明一个缓存容器
    private final Map<String, Object> CACHE_CONTAINER = new HashMap<>();

    // 声明切面表达式
    public static final String POINTCUT_DEPT_UPDATE =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.updateById(..))";
    public static final String POINTCUT_DEPT_GET =
            "execution(* com.warehouse.sys.service.impl.DeptServiceImpl.getOne(..))";
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
        if (null != res1)
            return res1;
        else {
            // 没有的话取数据库中查询
            Dept res2 = (Dept) joinPoint.proceed();
            // key值取前缀+数据的id
            CACHE_CONTAINER.put(CACHE_DEPT_PREFIX + res2.getId(), res2);
            return res2;
        }
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
        DeptVo deptVo = (DeptVo) joinPoint.getArgs()[0];
        Boolean updateSuccess = (Boolean) joinPoint.proceed();
        if (updateSuccess) {
            // 数据库更新成功，更新缓存
            // 取出数据
            Dept oldDept = (Dept) CACHE_CONTAINER.get(CACHE_DEPT_PREFIX + deptVo.getId());
            // 如果缓存中没有就new一个
            Dept newDept = oldDept == null ? new Dept() : oldDept;
            // 复制更新后的数据
            BeanUtils.copyProperties(deptVo, newDept);
            // 更新数据缓存
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
            CACHE_CONTAINER.remove(CACHE_DEPT_PREFIX + id);
        }
        return removeSuccess;
    }
}

```

这个切面暂时只是代理了 sys_dept 中的数据，具体的用法以后再说

---

### 2.10. 菜单管理系统

和部门很像啦，复制粘贴替换就好啦

有几个问题在这里需要解决，我们的编辑按钮在点击之后会自动选中的父级节点，可再次点击根节点的时候，父级节点并没有消失，经测试这里的父级节点 id 确实已经替换成了根节点的父级节点 id，但是文字依然显示上一次点击的节点的父级节点，查阅文档后发现 dtree 的下拉树有一个重置选中节点的方法`selectResetVal()`，那么只需要在打开弹出层成功后执行一次重置即可。

```js
// 每次打开重置下拉树的节点选择
selectTree.selectResetVal();
```

在删除菜单时要注意角色数据是依赖于菜单的，我们也要删除所有和该菜单对应的角色菜单关系数据，我们在后台删除菜单调用的是`pemissionService.removeById()`的方法，这里我们打开实现类重写这个方法

```java
    @Override
    public boolean removeById(Serializable id) {
        // 删除角色权限关系表中的pid对应的所有数据
        int deleteById = baseMapper.deleteRolePermissionByPid(id);
        return super.removeById(id);
    }
```

在 Mapper 中新添这个`deleteRolePermissionByPid(Serializable pid)`方法

```java
int deleteRolePermissionByPid(@Param("pid") Serializable pid);
```

PermissionMapper.xml 实现

```xml
    <update id="deleteRolePermissionByPid">
        delete from sys_role_permission where pid=#{pid}
    </update>
```

删除表格内容时会碰到这样的情况，删除了最后一页的唯一一个数据，表格应该刷新数据并跳转到前一页，我们需要在 js 渲染表格的代码后加上这一段

```js
var tableIns = table.render({
  // 当删除本页最后一个数据时需要把页面向前加载一页
  done: function(res, curr, count) {
    if (res.data.length == 0 && curr != 1)
      tableIns.reload({
        page: {
          curr: curr - 1
        }
      });
  }
});
```

最终效果：

![image.png](https://i.loli.net/2020/03/09/zmHpx1g3niSUPWB.png)

重复功能不再一一上图

---

### 2.11. 权限管理系统

和菜单管理很像，数据库查询时修改查询 type 为 permission 即可

最终效果：

![image.png](https://i.loli.net/2020/03/09/K2JCRIqvrQWiuVz.png)

---

### 2.12. 角色管理系统

当你真的搭建完之后……你也就不想写开发日志了……下面我尽量吧……有时间再说

最终效果：

![image.png](https://i.loli.net/2020/03/09/c7WkPwEqn5CGIDL.png)

为角色分配权限：

![image.png](https://i.loli.net/2020/03/09/8MLiTNj7OxJ3hgm.png)

---

### 2.13. 用户管理系统

最终效果：

王五登录后的菜单没有全部的功能

![image.png](https://i.loli.net/2020/03/09/gRF4EnKH3mzkMe2.png)

在 html 中添加 shiro 和 thymeleaf 的标签

```html
<html
  xmlns:th="http://www.thymeleaf.org"
  xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"
></html>
```

然后在按钮上添加`shiro:hasPermission="user:update"`

```html
<button
  shiro:hasPermission="user:update"
  type="button"
  lay-event="edit"
  class="layui-btn layui-btn-sm layui-btn-warm"
>
  <span class="layui-icon layui-icon-edit"></span>编辑
</button>
```

举例：我们用超级管理员登录给王五的基础数据管理角色去掉**添加用户**和**编辑用户**的权限，王五登录后就没有**添加用户**和**编辑用户**的按钮了

![image.png](https://i.loli.net/2020/03/09/go7A3Knde6jhQfD.png)

---

> 有时间我再看看，完善一下开发日志……(摸了摸了)拜拜！
