package cn.nio.net_connection.client_zh.demo;

import cn.nio.net_connection.client_zh.ReceiveObject;
import cn.nio.net_connection.client_zh.client.ClientCenter;
import cn.nio.net_connection.common.point.Config;
import cn.nio.net_connection.common.tool.SnowflakeIdWorker;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        String to="88";
        int port = 20000;
        String serverIp = "47.105.174.120";

        Config config = new Config();
        String top[] = {to};
        config.setToPoint(top);
        config.setPort(port);
        config.setServerIp(serverIp);
        config.setPoint(SnowflakeIdWorker.getLocalIp());
        ClientCenter clientCenter = ClientCenter.getInstence(config, new ReceiveObject() {
            @Override
            public void receive(String object) {
                System.out.println("客户端收到："+object);
            }
        });

        while (true){
            clientCenter.sendMessageToAll(config.getPoint()+"发送到88的消息"+System.currentTimeMillis());
            Thread.sleep(1000);
        }
    }
}
