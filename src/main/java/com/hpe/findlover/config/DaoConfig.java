package com.hpe.findlover.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/*
* 数据源配置
* */
@Configuration
@EnableTransactionManagement/* 开启事务支持*/
public class DaoConfig {

	/* 使用bean注解生成 DruidDataSource实例并注册到IOC容器中。*/
	@Bean(name = "DruidDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.dbcp2")
	public DataSource getDataSource(){
		return new DruidDataSource();
	}

	@Bean
	public DataSourceTransactionManager getDataSourceTransactionManager(){
		return new DataSourceTransactionManager(getDataSource());
	}
}
