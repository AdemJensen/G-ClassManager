package top.grapedge.gclass.server.base;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description: 服务端基类
 * @author: Grapes
 * @create: 2019-03-14 12:21
 **/
public abstract class GServer {
    protected List<ClientHandler> clients;
    protected ServerSocket serverSocket;

    public GServer(int port) throws IOException {
        clients = new ArrayList<>();
        serverSocket = new ServerSocket(port);
    }

    /**
     * 服务更新
     */
    public abstract void update();

    /**
     * 从客户端列表中移除
     * @param des 移除的客户端
     */
    public void remove(Object des) {
        clients.remove(des);
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}
