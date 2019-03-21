package top.grapedge.gclass.server.json;

import top.grapedge.gclass.base.Parser;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 14:54
 **/
public class Json {
    @Override
    public String toString() {
        return Parser.toJson(this);
    }
}
