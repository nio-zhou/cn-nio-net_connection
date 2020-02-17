package cn.nio.net_connection.server_zh.server;


/**
 * @ClassName Listener
 * @Author nio
 * @Description //TODO $
 * @Date $ $
 **/
public interface ListenerInConnect {

    ListenerInConnect STATIC_LISTENER_CONNECT = new ListenerInConnect() {
        @Override
        public void onConnect(InConnection connection) {

        }

        @Override
        public void onDisConnect(InConnection connection) {

        }
    };

    default void onConnect(InConnection connection){
    }

    default void onDisConnect(InConnection connection){
    }


}
