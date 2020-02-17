
package cn.nio.net_connection.client_zh.client;

import cn.nio.net_connection.client_zh.Receive;
import cn.nio.net_connection.common.Encoder.ProxyMess;
import cn.nio.net_connection.common.point.Config;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserClient extends ChannelInboundHandlerAdapter {

    private Config config;

    private Channel channel;

    private Receive receive;

    private Boolean isOn;

    static final AttributeKey<String> clientId =AttributeKey.newInstance("clientId");

    static Logger logger = LoggerFactory.getLogger(UserClient.class);

    protected UserClient(Config config, Receive receive) {
        this.receive = receive;
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMess mess = (ProxyMess) msg;
        logger.debug("收到消息，type:{} value:{} mesId:{} outId:{}",mess.getType(),mess.getValue(),mess.getMessageId(),mess.getOutId());
        switch (mess.getType()){
            case Connect:{
                register(mess);
            }break;
            case data:{
                receive.receive(mess);
            }break;
            case dataall:{
                receive.receive(mess);
            }break;
            case disConnect:{
                logger.error("服务器上传来关闭");
                ctx.close();
                System.exit(0);
            }break;
            default:{
                logger.error("收到异常消息，关闭连接");
                ctx.close();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("建立连接");
        String sessionId = ctx.channel().attr(clientId).get();
        this.channel = ctx.channel();
        login();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(!isOn){
            logger.debug("连接超时");
            close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        logger.debug("关闭");
    }

    protected void register(ProxyMess mess){
        isOn = true;
        UserClient userClient = ClientCenter.client;
        if(userClient!=null){
            userClient.close();
        }
        this.isOn = true;
        ClientCenter.client = this;
    }

    public void close(){
        channel.close();
    }
    public void clean(){
        try{
            channel.close();
        }catch (Exception e){}
        channel=null;
        receive=null;
        isOn=false;
        if( ClientCenter.client == this){
            ClientCenter.client=null;
        }

    }

    private void login(){

        ProxyMess mess = new ProxyMess();
        String toPoint = "";
        if(config.getToPoint()!=null) {
            for (String to : config.getToPoint()) {
                if (!toPoint.isEmpty()) {
                    toPoint = ",";
                }
                toPoint = toPoint + to;
            }
        }
        String loginStr = config.getSecurity()+"\n" +toPoint;
        mess.setValue(loginStr);
        mess.setType(ProxyMess.ConnetType.Connect);
        mess.setOutId(config.getPoint());
        channel.writeAndFlush(mess);

    }

    public void send(ProxyMess object){
        channel.writeAndFlush(object);
    }


}
