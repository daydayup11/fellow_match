package org.mumu.user_centor.service.chatService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.config.HttpSessionConfigurator;
import org.mumu.user_centor.constant.ChatConstant;
import org.mumu.user_centor.constant.UserConstant;
import org.mumu.user_centor.controller.UserController;
import org.mumu.user_centor.model.domain.Im;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.ImMessageVo;
import org.mumu.user_centor.service.ImService;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.utils.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/im")
@Component
public class ChatWebSocketServer {

    private static ApplicationContext applicationContext;
//    public static void setApplicationContext(ApplicationContext context){
//        applicationContext = context;
//    }
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketServer.class);
    /**
     * 记录当前在线连接数
     * key - 用户id , value - 用户会话
     */
    public static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    public static final Gson gson = new Gson();

    @Resource
    ImService imService;
    @Resource
    UserService userService;

    private static ImService staticImService;
    private static UserService staticUserService;

    // 程序初始化的时候触发这个方法 赋值
    @PostConstruct
    public void setStaticUser() {
        staticImService = imService;
        staticUserService = userService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        User loginUser = UserHolder.getUser();
        if(loginUser == null){
            return;
        }
        sessionMap.put(loginUser.getId(), session);
        log.info("有新用户加入，uid={}, 当前在线人数为：{}", loginUser.getId(), sessionMap.size());
        Im im = new Im();
        im.setText(gson.toJson(sessionMap.keySet()));
        ImMessageVo imMessageVo = new ImMessageVo();
        imMessageVo.setType(ChatConstant.CHAT_LOGIN_LIST);
        imMessageVo.setIm(im);
        sendAllMessage(gson.toJson(imMessageVo));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        User loginUser = UserHolder.getUser();
        sessionMap.remove(loginUser.getId());
        log.info("有一连接关闭，uid={}的用户session, 当前在线人数为：{}", loginUser.getId(), sessionMap.size());
        Im im = new Im();
        im.setText(gson.toJson(sessionMap.keySet()));
        ImMessageVo imMessageVo = new ImMessageVo();
        imMessageVo.setType(ChatConstant.CHAT_LOGIN_LIST);
        imMessageVo.setIm(im);
        // 后台发送消息给所有的客户端
        sendAllMessage(gson.toJson(imMessageVo));
    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session fromSession) throws JsonProcessingException {
        try {
            // 处理msg
            // 存储数据库
            Im im = gson.fromJson(message, Im.class);
            log.info("服务端收到用户uid={}的消息:{}", im.getUid(), message);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",im.getUid());
            long count = staticUserService.count(queryWrapper);
            if(count <= 0){
                return;
            }
            if(StringUtils.isBlank(im.getText())){
                return;
            }
            //消息最大长度为40
            if(im.getText().length()>40){
                return;
            }
            // 存储数据到数据库
            staticImService.save(im);
            ImMessageVo imMessageVo = new ImMessageVo();
            imMessageVo.setType(ChatConstant.CHAT_TYPE);
            imMessageVo.setIm(im);
            this.sendMessage(imMessageVo, message);
//            fromSession.getBasicRemote().sendText(message);
            log.info("发送消息：{}", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给目标用户
     */
    private void sendMessage(ImMessageVo imMessageVo, String message) {
        try {
            Long toId = imMessageVo.getIm().getToId();
            for (Map.Entry<Long, Session> imUserSessionEntry : sessionMap.entrySet()) {
                Long id = imUserSessionEntry.getKey();
                if (id.equals(toId)) {
                    Session session = imUserSessionEntry.getValue();
                    session.getBasicRemote().sendText(message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void   sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}
