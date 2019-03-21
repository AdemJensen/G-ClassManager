package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description: 文件数据
 * @author: Grapes
 * @create: 2019-03-14 15:54
 **/
public class FileJson extends Json {
    public int fileId = -1;
    public String name = "";
    public String path = "";
    public String owner = "-1";
    public long length = 0;
    public String classId;

    public static FileJson parse(String json) {
        return Parser.fromJson(json, FileJson.class);
    }
}
