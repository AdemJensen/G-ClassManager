package top.grapedge.gclass.server.server;

import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.server.base.GServer;
import top.grapedge.gclass.server.handler.info.InfoClientHandler;
import top.grapedge.gclass.server.handler.info.InfoMessageHandler;

import java.io.IOException;

/**
 * @program: G-ClassManager
 * @description: 用于处理实时聊天消息
 * @author: Grapes
 * @create: 2019-03-14 12:21
 **/
public class GInfoServer extends GServer {

    public GInfoServer(int port) throws IOException {
        super(port);
    }

    // 用于更新
    @Override
    public void update() {
        while (true) {
            try {
                var handler = new InfoClientHandler(new GSocket(serverSocket.accept()), this, new InfoMessageHandler());
                handler.start();
                clients.add(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
