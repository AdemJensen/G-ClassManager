package top.grapedge.gclass.server.base;

import top.grapedge.gclass.base.net.GSocket;

/**
 * @program: G-ClassManager
 * @description: 处理客户端抽象类
 * @author: Grapes
 * @create: 2019-03-14 12:26
 **/
public abstract class ClientHandler {
    protected GServer server;
    protected GSocket client;
    protected MessageHandler messageHandler = null;

    public ClientHandler(GSocket socket, GServer server) {
        this.client = socket;
        this.server = server;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void start() {
        update();
    }

    /**
     * 更新处理
     */
    public abstract void update();
}
