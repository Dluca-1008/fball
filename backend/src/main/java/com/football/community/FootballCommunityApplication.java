package com.football.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.football.community.repository")
@EnableScheduling
public class FootballCommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(FootballCommunityApplication.class, args);
    }
}
