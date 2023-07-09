package com.hpe.findlover.config;

import com.hpe.findlover.interceptor.UserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 拦截器配置类
 */
@Configuration
public class ClientResourcesConfig extends WebMvcConfigurerAdapter {
    /**
     * 该拦截器添加到拦截器配置中
     * 该方法作用在调用Controller方法的参数传入之前
     * @param  argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserHandlerMethodArgumentResolver());
    }
}