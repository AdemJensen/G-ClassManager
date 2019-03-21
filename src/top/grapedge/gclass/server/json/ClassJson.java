package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 23:11
 **/
public class ClassJson extends Json {
    public String classid;
    public int gradeIndex;
    public int classIndex;
    public int avatar = -1;
    public String intro;

    public static ClassJson parse(String json) {
        return Parser.fromJson(json, ClassJson.class);
    }

    public ClassJson() {}

    public ClassJson(String classid) {
        this.classid = classid;
    }
}
