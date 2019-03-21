package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-15 11:45
 **/
public class MessageJson extends Json {
    public String sender;
    public String receiver;
    public String text;
    public int msgid;
    public long time;
    public int type;

    public static MessageJson parse(String json) {
        return Parser.fromJson(json, MessageJson.class);
    }
}
