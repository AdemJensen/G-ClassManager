package top.grapedge.gclass.client.base;

import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.json.MessageJson;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-18 10:04
 **/
public class GChatClient {
    private static GChatClient instance;
    private GSocket socket;
    private int token = -12222;
    private List<GListener> msgListener = new ArrayList<>();
    public void addMsgListener(GListener listener) {
        msgListener.add(listener);
    }

    public static GChatClient getInstance() {
        return instance;
    }

    private Thread readThread = new Thread(()-> {
        try {
            while (true) {
                var msg = MessageJson.parse(Message.parse(socket.readUTF()).props);
                for (var i : msgListener) {
                    i.onReceiveMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    public GChatClient() throws IOException {
        instance = this;
        socket = new GSocket(new Socket(GClass.HOST, GClass.CHAT_PORT));
        readThread.start();
    }

    public void register(int token) {
        this.token = token;
        try {
            socket.writeUTF(new Message(2, token, "").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(MessageJson messageJson) {
        try {
            socket.writeUTF(new Message(0, token, messageJson.toString()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
