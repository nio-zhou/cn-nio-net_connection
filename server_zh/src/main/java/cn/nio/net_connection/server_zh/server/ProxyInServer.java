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

import cn.nio.queue.common.Encoder.ProxyMessDecoder;
import cn.nio.queue.common.Encoder.ProxyMessEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author nio
 * @Description
 * @Date 13:44 2020/1/3
 * @Param
 * @return
 **/
public final class ProxyInServer {
    public static int port = 20000;
    public static String security = "";

    static Logger logger = LoggerFactory.getLogger(ProxyInServer.class);
    private ProxyInServer() {
    }

    public static void start(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.option(ChannelOption.SO_BACKLOG, 65535);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProxyMessDecoder());
                    ch.pipeline().addLast(new ProxyMessEncoder());
//                    ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//                    ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                    ch.pipeline().addLast("heartBeatHandler", new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("handler", ProxyInServerHandler.INSTANCE);
                }
            });
            logger.debug("消息服务启动：{}",port);
            ChannelFuture f = b.bind(port).sync();
            logger.debug("消息服务启动：{}",port);

            f.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error("",e);
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
