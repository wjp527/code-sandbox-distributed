package com.wjp.wojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// 屏蔽mysql自动配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// ✨✨✨
@EnableDiscoveryClient // @EnableDiscoveryClient 注解开启服务注册与发现功能
public class WOjBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WOjBackendGatewayApplication.class, args);
    }

}
