package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description: 用户信息表
 * @author: Grapes
 * @create: 2019-03-14 13:42
 **/
public class UserJson extends Json{
    public String userid;
    public String password;
    public String username;
    public int avatar;
    public int status;

    public static UserJson parse(String json) {
        return Parser.fromJson(json, UserJson.class);
    }

    public UserJson() {}
    public UserJson(String userid) {
        this.userid = userid;
    }
}
