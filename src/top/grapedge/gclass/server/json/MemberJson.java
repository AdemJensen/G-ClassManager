package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-18 10:19
 **/
public class MemberJson extends Json{
    public String userId;
    public String classId;
    public int type;

    public static MemberJson parse(String json) {
        return Parser.fromJson(json, MemberJson.class);
    }
}
