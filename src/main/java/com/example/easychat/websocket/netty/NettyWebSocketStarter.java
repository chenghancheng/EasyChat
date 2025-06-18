package com.example.easychat.websocket.netty;

import com.example.easychat.config.Appconfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;



@Component
public class NettyWebSocketStarter implements Runnable {

    //接受客户端的连接请求
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();

    //处理连接后的IO事件
    private static EventLoopGroup workGroup = new NioEventLoopGroup();

    @Resource
    Appconfig appconfig;


    @Resource
    private HandlerWebSocket handlerWebSocket;

    @PreDestroy
    private void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    @Override
    public void run() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用bossGroup和workGroup分工协作
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    //设置几个重要的处理器
                    //对http协议的支持，使用http协议的编码器、解码器
                    pipeline.addLast(new HttpServerCodec());
                    //保证接受Http的完整性
                    pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                    //心跳
                    pipeline.addLast(new IdleStateHandler(6, 0, 0, TimeUnit.SECONDS));
                        //心跳处理的自定义处理器
                    pipeline.addLast(new HandlerHeartBeat());

                    //将http协议升级为ws协议，对websocket支持
                    pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536, true, true, 10000L));
                        //自定义的ws处理器
                    pipeline.addLast(handlerWebSocket);
                }
            });
            Integer wsPort = appconfig.getWsPort();  // 默认端口，假设 appconfig 返回了默认端口
            String wsPortStr = System.getProperty("ws.port");
            if (wsPortStr != null && !wsPortStr.isEmpty()) {
                wsPort = Integer.parseInt(wsPortStr);
            } else {
                System.out.println("使用默认端口: " + wsPort);
            }
            ChannelFuture channelFuture = serverBootstrap.bind(appconfig.getWsPort()).sync();
            System.out.println("netty服务启动成功，端口为:" + appconfig.getWsPort());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("启动netty失败" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
