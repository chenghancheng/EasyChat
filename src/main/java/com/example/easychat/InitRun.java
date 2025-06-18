package com.example.easychat;

import com.example.easychat.redis.RedisUtils;
import com.example.easychat.websocket.netty.NettyWebSocketStarter;
import io.lettuce.core.RedisConnectionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@Component("initRun")
public class InitRun implements ApplicationRunner {
    @Resource
    private DataSource dataSource;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private NettyWebSocketStarter nettyWebSocketStarter;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dataSource.getConnection();
            redisUtils.get("test");

            new Thread(nettyWebSocketStarter).start();

        } catch (SQLException e) {
            System.out.println("数据库配置错误");
        } catch (RedisConnectionFailureException e) {
            System.out.println("redis配置错误");
        } catch (Exception e) {
            System.out.println("服务启动失败");
        }
    }
}
