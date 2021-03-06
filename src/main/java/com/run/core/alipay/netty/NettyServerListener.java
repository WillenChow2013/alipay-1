package com.run.core.alipay.netty;

import com.fasterxml.jackson.core.ObjectCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyServerListener {

    @Value("${netty.port}")
    private int port;

    @Value("${netty.max-frame-len}")
    private int mxFrameLen;

    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通道适配器
     */

    @Resource
    private NettyServerHandler nettyServerHandler;

    @PreDestroy
    public void close() {
        log.info("准备关闭netty服务器...");
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }


    public void start() {
        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO));

        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // 添加心跳支持
                    pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));

                    //添加SSL双向证书
                    SSLEngine sslEngine = ContextSSLFactory.getSslContext().createSSLEngine();

                    sslEngine.setUseClientMode(false);
                    sslEngine.setNeedClientAuth(true);
                    pipeline.addLast("ssl", new SslHandler(sslEngine));

                    //数据发送编码器
                    pipeline.addLast(new ByteArrayEncoder());
                    //数据接收解码器
                    pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));

                    //业务处理
                    pipeline.addLast(nettyServerHandler);
                }
            });


            ChannelFuture f = serverBootstrap.bind(port).sync();
            log.info("netty服务已经启动,端口：{}", port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.info("netty服务启动出现异常异常，释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }


}
