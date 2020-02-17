package cn.nio.net_connection.server_zh;

import cn.nio.net_connection.server_zh.server.ProxyInServer;

public class ServerZhApplication {

    public static void main(String[] args) throws Exception {
        ProxyInServer.port=20000;

        ProxyInServer.start(args);
    }

}
