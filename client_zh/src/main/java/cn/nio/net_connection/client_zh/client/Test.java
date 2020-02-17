package cn.nio.net_connection.client_zh.client;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
////                    client(args);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();


    }

//    public static void client(String[] args) throws InterruptedException {
//        String to="88";
//        int port = 20021;
//        String serverIp = "127.0.0.1";
//
//        Config config = new Config();
//        String top[] = {to};
//        config.setToPoint(top);
//        config.setPort(port);
//        config.setServerIp(serverIp);
//        config.setPoint(SnowflakeIdWorker.getLocalIp());
//        ClientCenter clientCenter = ClientCenter.getInstence(config, new ReceiveObject() {
//            @Override
//            public void receive(String object) {
//                System.out.println("客户端收到："+object);
//            }
//        });
//
//        while (true){
//            clientCenter.sendMessageToAll(config.getPoint()+"发送到88的消息");
//            Thread.sleep(100);
//        }
//    }
//
//    public static void server(String[] args) throws InterruptedException {
//        String point="88";
//        int port = 20021;
//        String serverIp = "127.0.0.1";
//
//        Config config = new Config();
////        String top[] = {to};
////        config.setToPoint(top);
//        config.setPort(port);
//        config.setServerIp(serverIp);
//        config.setPoint(point);
//        ClientCenter clientCenter = ClientCenter.getInstence(config, new ReceiveObject() {
//            @Override
//            public void receive(String object) {
//                System.out.println("服务器收到："+object);
//            }
//        });
//
//        while (true){
//            clientCenter.sendMessageToAll(config.getPoint()+"发送到客户端的消息");
//            Thread.sleep(100);
//        }
//    }


}
