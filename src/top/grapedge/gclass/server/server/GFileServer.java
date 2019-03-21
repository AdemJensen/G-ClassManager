package top.grapedge.gclass.server.server;

import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.server.base.GServer;
import top.grapedge.gclass.server.handler.file.FileClientHandler;
import top.grapedge.gclass.server.handler.file.FileMessageHandler;

import java.io.IOException;

/**
 * @program: G-ClassManager
 * @description: 文件传输服务
 * @author: Grapes
 * @create: 2019-03-14 15:47
 **/
public class GFileServer extends GServer {
    public GFileServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void update() {
        while (true) {
            try {
                var handler = new FileClientHandler(new GSocket(serverSocket.accept()), this, new FileMessageHandler());
                handler.start();
                clients.add(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
