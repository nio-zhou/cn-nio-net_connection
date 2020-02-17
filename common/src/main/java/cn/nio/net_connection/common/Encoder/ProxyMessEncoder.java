package cn.nio.net_connection.common.Encoder;

import cn.nio.net_connection.common.tool.ClassToByte;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProxyMessEncoder extends MessageToByteEncoder<ProxyMess> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProxyMess proxyMess, ByteBuf out) throws Exception {
        try {
            byte[] body = proxyMess.toBytes();  //将对象转换为byte，伪代码，具体用什么进行序列化，你们自行选择。可以使用我上面说的一些
            int length = body.length;
            byte[] reBodys = new byte[5+length];
            byte[] lengthby = ClassToByte.intToByte(length);
            reBodys[0]=0x00;
            System.arraycopy(lengthby,0,reBodys,1,4);
            System.arraycopy(body,0,reBodys,5,length);
            out.writeBytes(reBodys);  //消息体中包含我们要发送的数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
