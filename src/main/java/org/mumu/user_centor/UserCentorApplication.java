package org.mumu.user_centor;

import org.mumu.user_centor.service.chatService.byNetty.ChatNettyWebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

@MapperScan("org.mumu.user_centor.mapper")
@SpringBootApplication
@EnableScheduling
//@EnableElasticsearchRepositories(basePackages="com.niuma.langbei.model.repository")
public class UserCentorApplication implements CommandLineRunner {
    @Resource
    ChatNettyWebSocketServer ChatNettyWebSocketServer;
    public static void main(String[] args) {
        SpringApplication.run(UserCentorApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        //启动聊天室服务 打开/templates/index.html
        ChatNettyWebSocketServer.run();
    }

}
