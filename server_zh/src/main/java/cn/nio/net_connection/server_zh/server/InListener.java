package cn.nio.net_connection.server_zh.server;


import cn.nio.queue.common.Encoder.ProxyMess;

/**
 * @ClassName Listener
 * @Author nio
 * @Description //TODO $
 * @Date $ $
 **/
public interface InListener {


    InListener STATIC_LISTENER = new InListener() {
        @Override
        public void onMessage(ProxyMess message) {

        }

        @Override
        public void onClose(InConnection connection) {

        }

    };

    void onMessage(ProxyMess message);

    default void onClose(InConnection connection){

    }

}
