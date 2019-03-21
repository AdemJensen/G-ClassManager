package top.grapedge.gclass.server.handler.info;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.ClientHandler;
import top.grapedge.gclass.server.base.GServer;
import top.grapedge.gclass.server.base.MessageHandler;

/**
 * @program: G-ClassManager
 * @description: 用于处理信息
 * @author: Grapes
 * @create: 2019-03-14 12:33
 **/
public class InfoClientHandler extends ClientHandler {

    public InfoClientHandler(GSocket socket, GServer server, MessageHandler infoHandler) {
        super(socket, server);
        this.messageHandler = infoHandler;
    }

    @Override
    public void update() {
        new Thread(()-> {
            try {
                while (true) {
                    // 获得消息
                    var request = Message.parse(client.readUTF());
                    // 获取执行结果
                    var result = messageHandler.handleMessage(request);
                    // 将结果写入
                    if (result != null) client.writeUTF(result.toString());
                    else client.writeUTF("");
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Debug.log(e.getMessage());
                ((InfoMessageHandler)messageHandler).logout();
                server.remove(this);
            }
        }).start();
    }
}
