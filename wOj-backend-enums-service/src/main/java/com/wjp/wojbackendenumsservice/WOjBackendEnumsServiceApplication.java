package com.wjp.wojbackendenumsservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wjp") // ✨✨✨加上这句之后，就可以扫描到其他模块的类了
public class WOjBackendEnumsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WOjBackendEnumsServiceApplication.class, args);
    }

}
