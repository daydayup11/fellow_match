package org.mumu.user_centor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("org.mumu.user_centor.mapper")
@SpringBootApplication
@EnableScheduling
public class UserCentorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCentorApplication.class, args);
    }

}
