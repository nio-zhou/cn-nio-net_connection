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
package cn.nio.net_connection.client_zh.client;

import cn.nio.net_connection.client_zh.Receive;
import cn.nio.net_connection.common.Encoder.ProxyMessDecoder;
import cn.nio.net_connection.common.Encoder.ProxyMessEncoder;
import cn.nio.net_connection.common.point.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public final class UserClientUtil {

    static Logger logger = LoggerFactory.getLogger(UserClientUtil.class);

    static EventLoopGroup workerGroup = new NioEventLoopGroup(2);
    static Bootstrap b = new Bootstrap();
    static IdleStateHandler idleStateHandler = new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS);
    static{
        b.group(workerGroup).channel(NioSocketChannel.class);
    }
    public static UserClient createClient(Config config, Receive sendAck) {
        UserClient userClient = new UserClient(config,sendAck);
        try {
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProxyMessDecoder());
                    ch.pipeline().addLast(new ProxyMessEncoder());
                    ch.pipeline().addLast("heartBeatHandler",new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("handler", userClient);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ChannelFuture f = b.connect(config.getServerIp(), config.getPort());
            f.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        logger.error("连接断开了");
                        userClient.clean();
                    }
                }
            });
            f.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        ClientCenter.clientCenter.init();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userClient;
    }



}
