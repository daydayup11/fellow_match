package org.mumu.user_centor.once;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mumu.user_centor.constant.RedisConstant.USER_GEO_KEY;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InsertUsersTest {

    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    private ExecutorService executorService = new ThreadPoolExecutor(40,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

//    @Test
//    public void doInsertUsers(){
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM=100000;
//        List<User> userList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假数据");
//            user.setUserAccount("fakeaccount");
//            user.setAvatarUrl("https://img1.baidu.com/it/u=1645832847,2375824523&fm=253&fmt=auto&app=138&f=JPEG?w=480&h=480");
//            user.setGender(0);
//            user.setUserPassword("231313123");
//            user.setPhone("1231312");
//            user.setEmail("12331234@qq.com");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setTags("[]");
//            userList.add(user);
//        }
//        userService.saveBatch(userList,10000);
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }

    @Test
    void loadUserData() {
        // 1.查询信息
        List<User> list = userService.list();
        for (User user : list) {
            Long id = user.getId();
            String key = USER_GEO_KEY;
            stringRedisTemplate.opsForGeo().add(key,new Point(user.getX(),user.getY()),user.getId().toString());
//            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(list.size());
//            locations.add(new RedisGeoCommands.GeoLocation<>(
//                    user.getId().toString(),
//                    new Point(user.getX(), user.getY())
//            ));
//            stringRedisTemplate.opsForGeo().add(key, locations);
        }
    }

}