package cn.nio.net_connection.server_zh.server;


import cn.nio.queue.common.Encoder.ProxyMess;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @ClassName Connection
 * @Author nio
 * @Description //TODO 客户端连接类型
 * @Date $ $
 **/
public class InConnection {
    static Logger logger = LoggerFactory.getLogger(InConnection.class);
    public InConnection() {
    }

    private String point;
    private String[] toPoint;

    private Channel channel;

    private String sessionId;

    private InListener listener = new InListener() {
        @Override
        public void onMessage(ProxyMess message) {
            channel.writeAndFlush(message);
        }

        @Override
        public void onClose(InConnection connection) {
            unlisten(connection.getListener());
            connection.unlisten(listener);
        }
    };

    public InListener getListener() {
        return listener;
    }



    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String[] getToPoint() {
        return toPoint;
    }

    public void setToPoint(String[] toPoint) {
        this.toPoint = toPoint;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getSessionId() {
        if(sessionId==null){
            if(channel==null){
            }else{
                sessionId=channel.id().asLongText();
            }
        }
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void close(){
        this.channel.close();
    }


    public InConnection(Channel channel) {
        this.channel = channel;
        this.sessionId = channel.id().asShortText();
        InConnection connection  = this;

        /**
         * @Author nio
         * @Description  关闭连接的时候调用关闭连接监听
         * @Date 9:06 2020/1/2
         * @Param [channel]
         * @return
         **/
        channel.closeFuture().addListeners(
            new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        ConnectionInSession.disConnect(connection.getSessionId());
                        try {
                            InListener.STATIC_LISTENER.onClose(connection);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        Iterator<InListener> iterator = listeners.iterator();
                        while (iterator.hasNext()){
                            InListener listener = iterator.next();
                            try {
                                listener.onClose(connection);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            //清理所有的监听
                            connection.listeners.clear();
                        } catch (Exception e) {

                        }
                    }
                }
            }
        );
    }


    HashSet<InListener> listeners= new HashSet();
    public void listen(InListener method){
        listeners.add(method);
    }
    public void unlisten(InListener method){
        listeners.remove(method);
    }

    /**
     * @Author nio
     * @Description  收到消息
     * @Date 9:06 2020/1/2
     * @return
     **/
    public void receive(ProxyMess message){
        try {
            InListener.STATIC_LISTENER.onMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(InListener listener:listeners){
            try {
                listener.onMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ChannelFuture sendMessage(ProxyMess message){
        return channel.writeAndFlush(message);
    }

    public boolean isOn=false;

}
