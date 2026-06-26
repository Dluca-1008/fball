package com.football.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.football.community.repository")
public class FootballCommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(FootballCommunityApplication.class, args);
    }
}
