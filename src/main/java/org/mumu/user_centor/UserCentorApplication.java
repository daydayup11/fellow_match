package org.mumu.user_centor;

import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.service.chatService.ChatWebSocketServer;
import org.mumu.user_centor.service.chatService.byNetty.ChatNettyWebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.List;

import static org.mumu.user_centor.constant.RedisConstant.USER_GEO_KEY;

@MapperScan("org.mumu.user_centor.mapper")
@SpringBootApplication
@EnableScheduling
public class UserCentorApplication implements CommandLineRunner {
    @Resource
    ChatNettyWebSocketServer ChatNettyWebSocketServer;
    public static void main(String[] args) {
        SpringApplication.run(UserCentorApplication.class, args);
//        ChatWebSocketServer.setApplicationContext(context);
    }
    @Override
    public void run(String... args) throws Exception {
        //启动聊天室服务 打开/templates/index.html
        ChatNettyWebSocketServer.run();
    }

}
