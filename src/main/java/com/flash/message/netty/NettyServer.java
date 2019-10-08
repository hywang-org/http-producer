package com.flash.message.netty;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.flash.message.config.Repository;
import com.flash.message.config.YPDao;
import com.flash.message.config.redis.AppInfoRedis;
import com.flash.message.config.redis.UserInfoRedis;
import com.flash.message.rabbitmq.service.RabbitmqService;
import com.flash.message.tabooword.core.TabooWordChecker;
import com.flash.message.utils.RedisOperationSets;
import com.flash.message.utils.es.ResultEsDao;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年7月1日 上午9:17:17
 *
 */
@Service
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Resource
    private RabbitmqService mqservice;

    @Resource
    private UserInfoRedis userRedis;

    @Resource(name = "redis_app")
    private AppInfoRedis appRedis;

    @Resource
    private YPDao ypDao;
    
    @Resource
    private ResultEsDao esDao;
    
    @PostConstruct
    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Map<String, Object> sourceMap = new HashMap<String, Object>();
        ServerBootstrap serverStrap = new ServerBootstrap();
        try {
            sourceMap.put("ypDao", ypDao);
            sourceMap.put("mqservice", mqservice);
            sourceMap.put("userRedis", userRedis);
            sourceMap.put("appRedis", appRedis);
            sourceMap.put("esDao", esDao);
            TabooWordChecker.init(ypDao);
            serverStrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    // ChannelOption.SO_BACKLO 设置SYNC 和 ACCEPT 队列最大能够接受大小
                    .option(ChannelOption.SO_BACKLOG, 1024 * 2)
                    // ChannelOption.RCVBUF_ALLOCATOR 表示缓存区动态自适应大小（适用于每次传输数据包大小差不多）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    // 缓冲区池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitHandler(sourceMap));

            int port = Repository.getIntProperty("nettyPort");
            ChannelFuture future = serverStrap.bind(port).sync();
            LOGGER.info("netty start success,port = {}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("netty server start error", e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            LOGGER.info("netty server shutdown");
        }
    }
}
