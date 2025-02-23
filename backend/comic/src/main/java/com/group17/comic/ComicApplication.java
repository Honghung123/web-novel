package com.group17.comic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.group17.comic.enums.AuthConfigProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({AuthConfigProperties.class})
public class ComicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComicApplication.class, args);
    }
}
