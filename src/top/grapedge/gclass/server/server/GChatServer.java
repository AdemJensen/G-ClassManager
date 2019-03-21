package top.grapedge.gclass.server.server;

import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.server.api.GGroup;
import top.grapedge.gclass.server.api.GMessage;
import top.grapedge.gclass.server.base.GServer;
import top.grapedge.gclass.server.handler.chat.ChatClientHandler;
import top.grapedge.gclass.server.handler.chat.ChatMessageHandler;
import top.grapedge.gclass.server.json.MemberJson;
import top.grapedge.gclass.server.json.MessageJson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-17 23:24
 **/
public class GChatServer extends GServer {
    private Map<String, ChatClientHandler> chatMap = new HashMap<>();
    private static GChatServer instance;

    public static GChatServer getInstance() {
        return instance;
    }

    public GChatServer(int port) throws IOException {
        super(port);
        instance = this;
    }

    @Override
    public void update() {
        while (true) {
            try {
                var handler = new ChatClientHandler(new GSocket(serverSocket.accept()), this, new ChatMessageHandler());
                handler.start();
                clients.add(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(String userId, ChatClientHandler handler) {
        logout(userId);     // 确保用户注销
        chatMap.put(userId, handler);
    }

    private void sendMessageToClient(String userId, MessageJson message) {
        // 如果用户在线
        if (chatMap.containsKey(userId)) {
            // 发送消息提醒
            var receiver = chatMap.get(userId);
            if (receiver == null) return;
            receiver.sendNewMessage(message);
        } else if (message.type == 0) {     // 确保是普通消息，群组消息此时已经在数据库中.
            // 用户离线，将消息添加至数据库
            GMessage.addMessage(message);
        }
    }

    public void sendMessage(MessageJson message) {
        if (message.type == 0) {
            sendMessageToClient(message.sender, message);
            sendMessageToClient(message.receiver, message);
        } else {
            GMessage.addMessage(message);       // 将群组消息添加到数据库
            List<MemberJson> members = GGroup.getMembers(message.receiver);
            for (var i : members) {
                sendMessageToClient(i.userId, message);
            }
        }
    }

    public void logout(String userId) {
        chatMap.remove(userId);
    }
}