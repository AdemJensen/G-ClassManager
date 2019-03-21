package top.grapedge.gclass.client.base;

import top.grapedge.gclass.server.json.MessageJson;

public interface GListener {
    void onReceiveMessage(MessageJson message);
}
