package com.qinyoucheng.auth.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.qinyoucheng.auth.system", "com.qinyoucheng.auth.common"})
@EnableDiscoveryClient
@MapperScan("com.qinyoucheng.auth.system.mapper")
public class AuthSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthSystemApplication.class, args);
    }
}
