package org.mumu.user_centor.controller;

import org.junit.jupiter.api.Test;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.service.impl.UserServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    @Resource
    UserService userService;

    @Test
    void matchUsers() {
        // 假设用户标签数据如下
        List<Set<String>> userTags = new ArrayList<Set<String>>();
        userTags.add(new HashSet<String>(){{add("游戏"); add("音乐"); add("电影");}});
        userTags.add(new HashSet<String>(){{add("美食"); add("旅游");}});
        userTags.add(new HashSet<String>(){{add("旅游"); add("运动");}});
        userTags.add(new HashSet<String>(){{add("游戏"); add("电影");}});
        userTags.add(new HashSet<String>(){{add("音乐"); add("电影");}});
        // 假设当前用户的标签数据如下
        Set<String> currentUserTags = new HashSet<String>(){{add("游戏"); add("电影");}};
//        userService.matchUsers(2,)

    }
}