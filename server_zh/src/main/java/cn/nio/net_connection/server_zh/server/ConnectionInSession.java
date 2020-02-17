package cn.nio.net_connection.server_zh.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @ClassName ConnectionOutSession
 * @Author nio
 * ashMap
 * @Description //TODO 连接会话
 * @Date $ $
 **/
public class ConnectionInSession {
    static Logger logger = LoggerFactory.getLogger(ConnectionInSession.class);

    /**CONNECTION SESSION,保存所有的连接会话**/
    public static final Map<String,InConnection> SESSION = new HashMap();

    public static final Map<String,InConnection> POINTSESSION = new HashMap();

    public static final Map<String, HashSet<String>> ROUTE = new HashMap();

    public static InConnection get(String sessionId){
        return  SESSION.get(sessionId);
    }


    public static void connect(InConnection object){
        SESSION.put(object.getSessionId(),object);
        logger.info("打开连接{},{}",object.getSessionId(),object.getChannel().remoteAddress());
    }

    public static boolean isOnPoint(String point){
        return POINTSESSION.get(point)!=null;
    }

    public static synchronized boolean register(InConnection object){
//        ROUTE.put(object.getSessionId(),object);
        //to
        logger.debug("节点 {} 连接到服务器",object.getPoint());
        if( POINTSESSION.get(object.getPoint())!=null){
            logger.debug("节点 {} 连接到服务器,发现却已经在线",object.getPoint());
            return false;
        }

        POINTSESSION.put(object.getPoint(),object);

        //找到所有我联系的人
        for(String to:object.getToPoint()){
            HashSet<String> toSet  = ROUTE.get(to);
            if(toSet==null){
                logger.debug("创建{} 的通讯录",to);
                toSet = new HashSet();
                ROUTE.put(to,toSet);
            }
            logger.debug("{}的通讯录,添加通信人{}",to,object.getPoint());
            toSet.add(object.getPoint());
            InConnection connection = POINTSESSION.get(to);

            if(connection!=null){
                //建立联系链路
                logger.debug("{}在线，{}直接与其连接 ",to,object.getPoint());
                connection.listen(object.getListener());
                object.listen(connection.getListener());
            }else{
                logger.debug("{}不在线，等待其上线找{} ",to,object.getPoint());
            }
        }


        //from
        Set<String> allfrom = ROUTE.get(object.getPoint());
        if(allfrom!=null) {
            logger.debug("{} 的通讯录不为空，去找等待的人",object.getPoint());
            Iterator<String> iterator = allfrom.iterator();
            while (iterator.hasNext()) {
                String from = iterator.next();
                InConnection connection = POINTSESSION.get(from);
                if (connection != null) {
                    //建立联系链路
                    logger.debug("{}与等待的人{}建立消息通信连接：",object.getPoint(),from);
                    object.listen(connection.getListener());
                    connection.listen(object.getListener());
                } else {
                    logger.debug("{}已经不在线了，通讯录删除",from);
                    allfrom.remove(from);
                }
            }
        }

        return true;
    }


    /**掉线或者断开连接**/
    protected static void disConnect(String sessionId){
        try {
            InConnection connection = SESSION.remove(sessionId);
            try {
                //找到所有我联系的人
//            logger.debug("{}下线，通知我联系的人 ",connection.getPoint());
                for (String to : connection.getToPoint()) {
                    HashSet<String> toSet = ROUTE.get(to);
                    if (toSet == null) {
                    } else {
                        //删除联系方式
                        logger.debug("{}下线，从{}的通讯录删除自己 ", connection.getPoint(), to);
                        toSet.remove(connection.getPoint());
                    }
                }
            }catch (Exception e){}

            POINTSESSION.remove(connection.getPoint());

            try{
                ListenerInConnect.STATIC_LISTENER_CONNECT.onDisConnect(connection);
            }catch (Exception e){

            }

            for(ListenerInConnect listener:listeners){
                try {
                    listener.onDisConnect(connection);
                } catch (Exception e) {
                }
            }

            connection.close();
        } catch (Exception e) {
        }



    }

    public static boolean isConnect(String sessionId){
        if(SESSION.get(sessionId)==null){
            return false;
        }else{
            return true;
        }
    }


    static final ArrayList<ListenerInConnect> listeners= new ArrayList();
    static public void listen(ListenerInConnect method){
        listeners.add(method);
    }
    static public void unlisten(ListenerInConnect method){
        listeners.remove(method);
    }



}
