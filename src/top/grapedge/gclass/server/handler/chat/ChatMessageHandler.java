package top.grapedge.gclass.server.handler.chat;

import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.GServerManager;
import top.grapedge.gclass.server.base.MessageHandler;
import top.grapedge.gclass.server.json.MessageJson;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-17 23:02
 **/
public class ChatMessageHandler extends MessageHandler {
    private ChatClientHandler clientHandler;

    public void setClientHandler(ChatClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public Message handleMessage(Message message) {
        switch (message.code) {
            case 0:
                var json = MessageJson.parse(message.props);
                clientHandler.sendMessageToServer(json);        // 向相应的用户发送提示信息
                break;
            case 2:
                // 注册事件，用于注册用户信息
                clientHandler.setUserId(GServerManager.getUserId(message.token));
                break;
        }
        return null;
    }
}
