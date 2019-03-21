package top.grapedge.gclass.server.handler.chat;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.ClientHandler;
import top.grapedge.gclass.server.base.GServer;
import top.grapedge.gclass.server.json.MessageJson;
import top.grapedge.gclass.server.server.GChatServer;

import java.io.IOException;

/**
 * @program: G-ClassManager
 * @description: 聊天服务端
 * @author: Grapes
 * @create: 2019-03-17 23:01
 **/
public class ChatClientHandler extends ClientHandler {
    private String userId = "-1000";

    public String getUserId() {
        return userId;
    }

    public ChatClientHandler(GSocket socket, GServer server, ChatMessageHandler handler) {
        super(socket, server);
        this.messageHandler = handler;
        ((ChatMessageHandler) this.messageHandler).setClientHandler(this);
    }

    @Override
    public void update() {
        new Thread(()-> {
            try {
                while (true) {
                    var message = Message.parse(client.readUTF());
                    messageHandler.handleMessage(message);
                }
            } catch (Exception e) {
                Debug.log(e.getMessage());
                ((GChatServer)server).logout(userId);
                server.remove(this);
            }
        }).start();
    }

    public void sendMessageToServer(MessageJson json) {
        ((GChatServer)server).sendMessage(json);
    }

    public void sendNewMessage(MessageJson message) {
        try {
            client.writeUTF(new Message(0, 0, message.toString()).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserId(String props) {
        userId = props;
        ((GChatServer)server).register(userId, this);
    }
}
