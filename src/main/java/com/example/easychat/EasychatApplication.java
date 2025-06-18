package com.example.easychat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = ("com.example.easychat.mapper"))
@EnableTransactionManagement
@EnableScheduling
public class EasychatApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasychatApplication.class, args);
    }

}
