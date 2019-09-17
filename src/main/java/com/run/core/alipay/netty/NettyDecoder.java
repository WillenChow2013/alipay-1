package com.run.core.alipay.netty;

import com.run.core.alipay.utils.ByteDataBuffer;
import com.run.core.alipay.utils.Compose;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class NettyDecoder extends MessageToMessageCodec<ByteBuf, Object> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        String msg = (String) o;

        ByteDataBuffer bdb = new ByteDataBuffer();
        bdb.setInBigEndian(false);
        bdb.writeInt8((byte) 0x68); // 开始字节
        int len = Integer.parseInt(Compose.getLen(msg, 4));
        // 用于计算数据域的长度是否大于4字节
        bdb.writeInt32(len); // 报文长度
        bdb.writeString("200001", 6);// 服务编码
        bdb.writeBytes(msg.getBytes()); // 报文frame
        bdb.writeInt8((byte) 0x16); // 结束字节

        list.add(bdb.getBytes());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];

        list.add(bytes);
    }
}
