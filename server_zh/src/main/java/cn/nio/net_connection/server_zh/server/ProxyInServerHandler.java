/*
 * Copyright 2019 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cn.nio.net_connection.server_zh.server;


import cn.nio.queue.common.Encoder.ProxyMess;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public final class ProxyInServerHandler extends ChannelInboundHandlerAdapter {
    static Logger logger = LoggerFactory.getLogger(ProxyInServerHandler.class);
    public static String password = "HMMY";


    public static final ProxyInServerHandler INSTANCE = new ProxyInServerHandler();

    private AttributeKey<String> ChannelId = AttributeKey.newInstance("stid");

    private ProxyInServerHandler() {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMess proxyMess = (ProxyMess) msg;
        String now = ctx.channel().id().asShortText();
        InConnection connection = ConnectionInSession.SESSION.get(now);
        if(connection==null){
            ctx.close();
            return;
        }
        logger.debug("收到消息，from：{} mid{} type:{} value:{}",connection.getPoint(),proxyMess.getMessageId(),proxyMess.getType(),proxyMess.getValue());
        excute(connection,proxyMess);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx)//链接事件监听
            throws Exception {
        //设置链接属性，记录链接开始的时间
        String now = ctx.channel().id().asShortText();
        ctx.channel().attr(ChannelId).set(now);
        InConnection connection =new InConnection(ctx.channel());
        ConnectionInSession.connect(connection );
    }

    @Override
    /***心跳事件***/
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        String now = ctx.channel().id().asShortText();
        InConnection connection = ConnectionInSession.SESSION.get(now);

        if(!ConnectionInSession.isConnect(now)){
            ctx.close();
        }

        if(connection==null||!connection.isOn){
            ctx.close();
        }
    }


    private void register(InConnection connection,ProxyMess mess){
        ProxyMess retainMess = new ProxyMess();
        if(mess.getOutId()==null||ConnectionInSession.isOnPoint(mess.getOutId())){
            logger.debug("节点 {} 连接到服务器,发现却已经在线",mess.getOutId());
            retainMess.setType(ProxyMess.ConnetType.disConnect);
            retainMess.setValue("有已经在线的同名节点");
            closeSend(connection,retainMess);
            return ;
        }


        String value = mess.getValue();
        String[] strs = value.split("\\n");
        String[] topoint = new String[0];

        if(strs.length==1){
            connection.setToPoint(topoint);
        }else if(strs.length==2){
            topoint = strs[1].split(",");
            connection.setToPoint(topoint);
        }else{
            logger.error("客户端连接方式错误");
            retainMess.setValue("客户端连接方式错误");
            closeSend(connection,retainMess);
            return;
        }
        String securityLogin = strs[0];
        if(ProxyInServer.security!=null&&!ProxyInServer.security.isEmpty()){
            if(!ProxyInServer.security.equals(securityLogin)){
                logger.error("密码错误");
                retainMess.setValue("密码错误");
                closeSend(connection,retainMess);
                return;
            }

        }

        connection.setPoint(mess.getOutId());
        if(!ConnectionInSession.register(connection)){
            connection.setToPoint(new String[0]);
            connection.setPoint(null);
            retainMess.setValue("注册失败");
            closeSend(connection,retainMess);
            return;
        }

        retainMess.setType(ProxyMess.ConnetType.Connect);
        connection.sendMessage(retainMess);
        connection.isOn=true;
    }

    void closeSend(InConnection connection,ProxyMess retainMess){
        connection.sendMessage(retainMess).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                connection.close();
            }
        });
        connection.close();
    }


    private void excute(InConnection connection,ProxyMess mess){

        switch (mess.getType()){
            case Connect:{
                register(connection,mess);
            }break;
            case data:{
                connection.receive(mess);
            }break;
            case dataall:{
                connection.receive(mess);
            }break;
            case test:{
                logger.error("收到心跳test{}",mess.getType());
            }break;
            default:{
                logger.error("收到异常消息{}，关闭连接");
                connection.close();
            }
        }

    }



}
