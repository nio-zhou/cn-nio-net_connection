package cn.nio.net_connection.common.point;

/**
 * 配置信息
 */
public class Config {
    /**
     * 服务器地址
     */
    private String serverIp;
    /**
     * 服务器端口
     */
    private int port;
    /**
     * 自己的节点 监听的节点
     * 如果服务器上发现重复的节点，将无法连接到服务器
     */
    private String point;
    /**
     * 我需要连接的多个节点
     */
    private String[] toPoint;
    /**
     * 服务器连接的密钥
     */
    private String security;


    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }
}
