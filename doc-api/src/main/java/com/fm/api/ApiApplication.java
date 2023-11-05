package com.fm.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.SchemaOutputResolver;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = {"com.fm.base.models.sql"})
@EnableScheduling
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("training run successfull!");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
