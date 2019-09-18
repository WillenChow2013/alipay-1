package com.run.core.alipay.netty;

import com.run.core.alipay.utils.ByteDataBuffer;
import com.run.core.alipay.utils.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RequestDispatcher implements ApplicationContextAware {

    @Value("${netty.max-threads}")
    private int maxThreads;

    private ExecutorService executorService = Executors.newFixedThreadPool(1024);
    private ApplicationContext app;

    public void dispatcher(final ChannelHandlerContext ctx, Object msg) {
        executorService.submit(() -> {
            ChannelFuture f = null;
            try {
                ByteDataBuffer bdf = new ByteDataBuffer(msg.toString().getBytes("UTF-8"));
                byte[] lenBytes = new byte[4];
                bdf.readBytes(lenBytes);
                String serverCode = bdf.readString(6);
                // String retData = bdf.readString(CommonUtil.readInt32(lenBytes));
                log.info("服务器接收到数据：{}", msg.toString());
                f = ctx.writeAndFlush(msg);
                f.addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.app = applicationContext;
    }
}
