package top.grapedge.gclass.server.base;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.server.server.GChatServer;
import top.grapedge.gclass.server.server.GFileServer;
import top.grapedge.gclass.server.server.GInfoServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: G-ClassManager
 * @description: 服务器管理器
 * @author: Grapes
 * @create: 2019-03-14 14:22
 **/
public class GServerManager {
    private static Map<Integer, String> tokens = new HashMap<>();

    public GServerManager() throws IOException {
        Debug.out("数据库已连接");
        var chatServer = new GChatServer(GClass.CHAT_PORT);
        new Thread(chatServer::update).start();
        Debug.out("聊天服务已启动");
        var fileServer = new GFileServer(GClass.FILE_PORT);
        new Thread(fileServer::update).start();
        Debug.out("文件服务已启动");
        // 启动信息传输服务
        var infoServer = new GInfoServer(GClass.INFO_PORT);
        new Thread(infoServer::update).start();
        Debug.out("信息服务已启动");
    }

    public static boolean containsToken(int key) {
        return tokens.containsKey(key);
    }

    public static String getUserId(int key) {
        return tokens.get(key);
    }

    /**
     * 增加用户至token列表
     * @param token 标识
     * @param userid 用户id
     */
    public static void addToken(int token, String userid) {
        tokens.put(token, userid);
    }

    public static void removeToken(String key) {
        tokens.remove(key);
    }
}
