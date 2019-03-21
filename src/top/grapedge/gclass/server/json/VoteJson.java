package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-21 13:49
 **/
public class VoteJson extends Json {
    public int voteId;
    public int noticeId;
    public String classId;
    public int agree = 0;
    public int disagree = 0;
    public int status = 0;      // 0 未开始，刚发布的状态， 1 开始了，审议状态， 2 审议结束，投票状态 3 投票结束，公示状态
    public long time = 0;
    public String member;

    public static VoteJson parse(String json) {
        return Parser.fromJson(json, VoteJson.class);
    }
    // (不进每个投票状态为24小时，为了方便演示这里调整为5分钟。
}
