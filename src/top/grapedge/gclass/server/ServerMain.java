package top.grapedge.gclass.server;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.server.base.Database;
import top.grapedge.gclass.server.base.GServerManager;

/**
 * @program: G-ClassManager
 * @description: 服务器主类
 * @author: Grapes
 * @create: 2019-03-14 13:20
 **/
public class ServerMain {
    public static void main(String[] args) {
        try {
            Debug.out("服务器已启动");
            var database = new Database();
            var serverManager = new GServerManager();
        } catch (Exception e) {
            e.printStackTrace();
            Debug.out("服务器出现错误");
        }
    }
}
