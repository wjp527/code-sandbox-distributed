package com.wjp.wojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.wjp.wojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.wjp") // ✨✨✨加上这句之后，就可以扫描到其他模块的类了
// ✨✨✨
@EnableDiscoveryClient // @EnableDiscoveryClient 注解开启服务注册与发现功能
// @EnableFeignClients 注解开启 Feign 客户端功能
// basePackages 指定扫描的包路径【FeignClient】
@EnableFeignClients(basePackages = {"com.wjp.wojbackendserviceclient"})
public class WOjBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WOjBackendUserServiceApplication.class, args);
    }

}
