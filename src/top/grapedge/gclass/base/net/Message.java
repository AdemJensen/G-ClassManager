package top.grapedge.gclass.base.net;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description: 基本通信定义
 * @author: Grapes
 * @create: 2019-03-11 15:48
 **/
public class Message {
    /**
     * code：通信代码，用于标识消息类型或者执行结果
     */
    public int code;
    /**
     * token：标识状态的附加值，如果有需要，用于验证身份
     */
    public int token;
    /**
     * props：此条消息附带的结果或者参数
     *        格式为JSON
     */
    public String props;

    public Message() {
        this(0, -1, "");
    }

    public Message(int code, String props) {
        this.code = code;
        this.props = props;
    }

    public Message(int code, int token, String props) {
        this(code, props);
        this.token = token;
    }

    // 转换为Json格式
    @Override
    public String toString() {
        return Parser.toJson(this);
    }

    public static Message parse(String json) {
        return Parser.fromJson(json, Message.class);
    }
}
