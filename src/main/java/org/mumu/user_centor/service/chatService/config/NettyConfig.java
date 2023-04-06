package org.mumu.user_centor.service.chatService.config;

import javax.annotation.Resource;
import io.netty.channel.Channel;
import org.mumu.user_centor.model.domain.ImUser;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 存储每个连接
 * @author niumazlb
 */
@Resource
public class NettyConfig {
    /**
     * 储存每个客户端接入进来的channel对象,如果需要额外信息可以改为map
     */
//    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<ImUser, Channel> idChannelGroup = new ConcurrentHashMap<>() ;
    public static Map<Channel, ImUser>  channelIdGroup = new ConcurrentHashMap<>() ;
}