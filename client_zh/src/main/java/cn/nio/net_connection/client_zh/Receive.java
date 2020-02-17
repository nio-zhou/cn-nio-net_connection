package cn.nio.net_connection.client_zh;

import cn.nio.net_connection.common.Encoder.ProxyMess;

public interface Receive {
    void receive(ProxyMess proxyMess);
}
