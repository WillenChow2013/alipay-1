package com.run.core.alipay.netty;

import com.run.core.alipay.service.impl.AliPayService;
import com.run.core.alipay.utils.ByteDataBuffer;
import com.run.core.alipay.utils.Cmd;
import com.run.core.alipay.utils.CommonUtil;
import com.run.core.alipay.utils.Compose;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AliPayService aliPayService;

    private ExecutorService executorService = Executors.newFixedThreadPool(1024);
    private ApplicationContext app;

    public void dispatcher(final ChannelHandlerContext ctx, Object msg) {
        executorService.submit(() -> {
            ChannelFuture f = null;
            try {
                log.info("服务器接收到报文：{}", msg.toString());
                ByteDataBuffer bdf = new ByteDataBuffer(msg.toString().getBytes("UTF-8"));

                bdf.setInBigEndian(false);
                int begin = bdf.readInt8();
                if(begin != 104) {
                    log.info("---------------------------------------------------------------------");
                    log.info("返回报文的开始字节格式有误");
                    log.info("---------------------------------------------------------------------");
                }

                byte[] lenBytes = new byte[4];
                bdf.readBytes(lenBytes);
                String serverCode = bdf.readString(6);
                String messageFrame = "";
                String retData = bdf.readString(CommonUtil.readInt32(lenBytes));

                log.info("---------------------------------------------------------------------");
                log.info("接收到的报文:" + retData);
                log.info("---------------------------------------------------------------------");

                if(Integer.valueOf(serverCode) == Cmd.CMD_ZFB_SEARCH)
                    messageFrame = aliPayService.service200001(retData);
                if(Integer.valueOf(serverCode) == Cmd.CMD_ZFB_PAY)
                    messageFrame = aliPayService.service200002(retData);
                if(Integer.valueOf(serverCode) == Cmd.CMD_ZFB_ACCOUNT)
                    messageFrame = aliPayService.service200012(retData);
                if(Integer.valueOf(serverCode) == Cmd.CMD_ZFB_STATEMENT)
                    messageFrame = aliPayService.service200011(retData);

                ByteDataBuffer bdb = new ByteDataBuffer();
                bdb.setInBigEndian(false);
                bdb.writeInt8((byte) 0x68); // 开始字节
                int len = Integer.parseInt(Compose.getLen(messageFrame, 4));
                // 用于计算数据域的长度是否大于4字节
                bdb.writeInt32(len); // 报文长度
                bdb.writeString(serverCode, 6);// 服务编码
                bdb.writeBytes(messageFrame.getBytes()); // 报文frame
                bdb.writeInt8((byte) 0x16); // 结束字节

                f = ctx.writeAndFlush(bdb.getBytes());
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
