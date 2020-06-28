package com.kongmu373.wxshop.config;


import com.kongmu373.wxshop.service.ShiroRealm;
import com.kongmu373.wxshop.service.VerificationCodeCheckService;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> pattern = new HashMap<>();
        pattern.put("/api/code", "anon");
        pattern.put("/api/login", "anon");
        pattern.put("/api/logout", "logout");
        pattern.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);

        return shiroFilterFactoryBean;
    }


    @Bean
    public DefaultWebSecurityManager mySecurityManager(ShiroRealm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        // 缓存
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        // 设置Session
        securityManager.setSessionManager(new DefaultSessionManager());
        return securityManager;
    }

}
