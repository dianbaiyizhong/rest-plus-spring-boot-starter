package com.nntk.restplus.sample;

import com.nntk.restplus.config.EnableRestPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableAsync
@EnableRestPlus // 重要注解，有了它才能启用功能
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
