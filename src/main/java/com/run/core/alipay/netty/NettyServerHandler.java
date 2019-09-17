package com.run.core.alipay.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.util.CharsetUtil.UTF_8;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

        log.info(msg.toString());

        ByteBuf in = (ByteBuf) msg;
        log.info("server received: {}", in.toString(UTF_8));

        channelHandlerContext.writeAndFlush("你好哟");

    }
}
