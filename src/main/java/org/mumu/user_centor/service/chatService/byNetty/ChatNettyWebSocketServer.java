package org.mumu.user_centor.service.chatService.byNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.mumu.user_centor.service.chatService.byNetty.handler.WebSocketServerInit;
import org.springframework.stereotype.Component;

@Component
public class ChatNettyWebSocketServer {

    public void run() {
        try {
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup work = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketServerInit());
            Channel channel = bootstrap.bind(8088).sync().channel();
            channel.closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
