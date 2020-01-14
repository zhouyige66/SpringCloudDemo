package cn.roy.springcloud.gateway.config;

import cn.roy.springcloud.gateway.shiro.ShiroCacheManager;
import cn.roy.springcloud.gateway.shiro.ShiroRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: Shiro配置
 * @Author: Roy Z
 * @Date: 2019-10-25 16:27
 * @Version: v1.0
 */
@Configuration
public class ShiroConfig {

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");// 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(1024);// 散列的次数，比如散列两次，相当于md5(md5(""));
        return hashedCredentialsMatcher;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    @Bean
    public ShiroRealm shiroRealm() {
        return new ShiroRealm();
    }

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Bean
    public ShiroCacheManager shiroCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ShiroCacheManager manager = new ShiroCacheManager(ehCacheManager, redisCacheManager);
        return manager;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 自定义realm;
        securityManager.setRealm(shiroRealm());
        // 缓存管理器;
        securityManager.setCacheManager(shiroCacheManager());
//        // session管理器
//        securityManager.setSessionManager(defaultWebSessionManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 不需要认证的URL
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/auth/**", "anon");
        // 需要认证的URL
        filterChainDefinitionMap.put("/user/**", "authc");
        // 退出系统的过滤器
        filterChainDefinitionMap.put("/logout", "logout");
        // 现在资源的角色
//        filterChainDefinitionMap.put("/admin.html", "roles[admin]");

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        // 设置Login URL
//        shiroFilterFactoryBean.setLoginUrl("/login.html");
        // 登录成功后要跳转的链接
//        shiroFilterFactoryBean.setSuccessUrl("/LoginSuccess.action");
        // 未授权的页面
//        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized.action");

        return shiroFilterFactoryBean;
    }

}
