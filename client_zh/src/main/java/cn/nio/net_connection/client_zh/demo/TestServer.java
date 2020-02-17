package cn.nio.net_connection.client_zh.demo;

import cn.nio.net_connection.client_zh.ReceiveObject;
import cn.nio.net_connection.client_zh.client.ClientCenter;
import cn.nio.net_connection.common.point.Config;

public class TestServer {
    public static void main(String[] args) throws InterruptedException {
        String point="88";
        int port = 20000;
        String serverIp = "47.105.174.120";

        Config config = new Config();

        config.setPort(port);
        config.setServerIp(serverIp);
        config.setPoint(point);

//        final int i=0;
        ClientCenter clientCenter = ClientCenter.getInstence(config, new ReceiveObject() {
            int i=0;
            @Override
            public void receive(String object) {
                i++;
                System.out.println("服务器收到："+object+"--"+(System.currentTimeMillis()));

            }
        });

        while (true){
            Thread.sleep(100000);
        }
    }
}
