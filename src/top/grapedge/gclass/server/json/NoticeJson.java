package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-20 19:47
 **/
public class NoticeJson extends Json {
    public int noticeId;
    public String sender;   // 发送者
    public String classId;      // 班级id
    public String text;     // 公告内容
    public long time;       // 公告时间

    public NoticeJson() {}

    public NoticeJson(int noticeId) {
        this.noticeId = noticeId;
    }

    public static NoticeJson parse(String json) {
        return Parser.fromJson(json, NoticeJson.class);
    }
}
