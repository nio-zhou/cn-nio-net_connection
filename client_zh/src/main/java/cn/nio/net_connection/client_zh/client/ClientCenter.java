package cn.nio.net_connection.client_zh.client;

import cn.nio.net_connection.client_zh.Receive;
import cn.nio.net_connection.client_zh.ReceiveObject;
import cn.nio.net_connection.common.Encoder.ProxyMess;
import cn.nio.net_connection.common.point.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName ClientCenter
 * @Author nio
 * @Description //TODO $
 * @Date $ $
 **/
public class ClientCenter {
    static Logger logger = LoggerFactory.getLogger(ClientCenter.class);

    static ClientCenter clientCenter = new ClientCenter();

    private Receive receive = new Receive() {
        @Override
        public void receive(ProxyMess proxyMess) {
            receiveObject.receive(proxyMess.getValue());
        }
    };
    private ReceiveObject receiveObject;

    public static ClientCenter getInstence(Config config, ReceiveObject receiveObject){
        clientCenter.config = config;
        clientCenter.receiveObject = receiveObject;
        clientCenter.init();
        return clientCenter;
    }

    protected  static  UserClient  client;
    protected  static boolean isConnecting = false;
    protected  static  int  connectTimes = 5;


    private Config config;
    static ExecutorService singleThread = Executors.newFixedThreadPool(1);

    public void init(){
        if(!isConnecting&&client==null){
            isConnecting = true;
            while(client==null) {
                if(client==null&&connectTimes>4){
                    logger.info("连接到服务器{} isConnecting:{} connectTimes:{}",client,isConnecting,connectTimes);
                    UserClientUtil.createClient(config, receive);
                    connectTimes=0;
                }
                connectTimes++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            isConnecting = false;

        }

    }

    public void sendMessageToAll(String object){
        if(client==null){
            logger.info("未连接到服务器");
            init();

        }else {
            ProxyMess proxyMess = new ProxyMess();
            proxyMess.setValue(object);
            proxyMess.setType(ProxyMess.ConnetType.dataall);
            client.send(proxyMess);
        }
    }

    public void sendMessageToSombd(String object,String sombd){
        ProxyMess proxyMess = new ProxyMess();
        proxyMess.setValue(object);
        proxyMess.setOutId(sombd);
        proxyMess.setType(ProxyMess.ConnetType.data);
        client.send(proxyMess);
    }
}
