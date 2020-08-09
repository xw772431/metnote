package com.metnote;

import com.metnote.repository.base.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Metnote main class.
 *
 * @author ryanwang
 * @date 2017-11-14
 */
@SpringBootApplication
@EnableAsync
@EnableJpaRepositories(basePackages = "com.metnote.repository", repositoryBaseClass = BaseRepositoryImpl.class)
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        // Customize the spring config location
//        System.setProperty("spring.config.additional-location", "file:${user.home}/.metnote/,file:${user.home}/metnote-dev/");

        // Run application
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        System.setProperty("spring.config.additional-location", "file:${user.home}/.metnote/,file:${user.home}/metnote-dev/");
        return application.sources(Application.class);
    }
}
