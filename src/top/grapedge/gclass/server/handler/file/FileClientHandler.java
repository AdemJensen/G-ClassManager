package top.grapedge.gclass.server.handler.file;

import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.ClientHandler;
import top.grapedge.gclass.server.base.GServer;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 15:49
 **/
public class FileClientHandler extends ClientHandler {
    public FileClientHandler(GSocket socket, GServer server, FileMessageHandler handler) {
        super(socket, server);
        this.messageHandler = handler;
    }

    @Override
    public void update() {
        new Thread(()-> {
            try {
                var message = Message.parse(client.readUTF());
                ((FileMessageHandler)messageHandler).setSocket(client);
                var result = messageHandler.handleMessage(message);
                if (result != null) client.writeUTF(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
                server.remove(this);
            }
        }).start();
    }
}
