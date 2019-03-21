package top.grapedge.gclass.server.base;

import top.grapedge.gclass.base.net.Message;

/**
 * @program: G-ClassManager
 * @description: 消息处理器
 * @author: Grapes
 * @create: 2019-03-14 12:40
 **/
public abstract class MessageHandler {
    public abstract Message handleMessage(Message message);
}
