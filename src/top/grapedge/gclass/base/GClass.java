package top.grapedge.gclass.base;

import javax.swing.text.html.HTMLEditorKit;
import java.util.Calendar;

/**
 * @program: G-ClassManager
 * @description: 全局变量
 * @author: Grapes
 * @create: 2019-03-11 16:36
 **/
public class GClass {

    public static final String HOST = "127.0.0.1";      // 主机地址
    public static final int CHAT_PORT = 2333;       // 实时聊天端口号
    public static final int FILE_PORT = 2334;       // 文件传输端口号
    public static final int INFO_PORT = 2335;       // 信息获取端口号

    // 消息定义，下面这些数字随便取的，不重复就行
    public static final int LOGIN = 0;       // 登录
    public static final int REGISTER = 1;       // 注册
    public static final int USER_INFO = 2;      // 用户信息
    public static final int CLASS_INFO = 3;     // 班级信息
    public static final int CHAT_MSG = 4;       // 获取用户聊天内容
    public static final int FILE_INFO = 5;      // 获取文件信息
    public static final int VOTE_INFO = 6;      // 获取投票信息
    public static final int NOTICE_INFO = 7;    // 获取公告信息
    public static final int VOTE = 8;       // 进行投票操作
    public static final int MY_CLASS = 9;       // 我的班级
    public static final int MY_CHAT = 10;       // 我的班级
    public static final int MEMBERS = 11;       // 获取成员列表
    public static final int CREATE_CLASS = 12;  // 创建班级
    public static final int ADD_CLASS = 13;     // 添加班级
    public static final int SET_ADMIN = 14;     // 设置管理员
    public static final int ADD_NOTICE = 15;     // 发布公告
    public static final int UPDATE_NOTICE = 16;     // 修改公告内容，用于投票的那个确认文稿
    public static final int NOTICE_LIST = 17;    // 获取公告信息
    public static final int ADD_VOTE = 18;      // 发布投票
    public static final int VOTE_LIST = 19;     // 获取投票列表
    public static final int SET_VOTE = 20;       // 开始投票
    public static final int ADD_ADVICE = 21;        // 添加建议
    public static final int ADVICE_LIST = 22;       // 建议列表
    public static final int FILE_LIST = 23;     // 文件列表
    public static final int ADD_FILE = 24;      // 添加文件

    /**
     * 缓存文件夹路径
     * @return
     */
    private static String getCachePath() {
        return System.getProperty("user.home") + "\\gclass";
    }

    /**
     * 将文件图片缓存至此文件夹
     * @return
     */
    public static String getFileCachePath() {
        return getCachePath() + "\\cache";
    }

    public static String getFileCachePath(String fileId) {
        return getFileCachePath() + "\\" + fileId;
    }

    /**
     * 得到聊天记录保存文件夹
     * @return
     */
    public static String getRecordPath() {
        return getCachePath() + "\\record";
    }

    /**
     * 得到聊天记录文件路径
     * @param id 用户id或者班级id
     * @return
     */
    public static String getRecordPath(String id) {
        return getRecordPath() + "\\" + id;
    }

    public static String getRecordPath(String userid, String sender) {
        return getRecordPath(userid) + "\\" + sender;
    }

    public static String getServerFileSavePath() {
        var cal = Calendar.getInstance();
        var year = cal.get(Calendar.YEAR);
        var month = cal.get(Calendar.MONTH);
        var date = cal.get(Calendar.DATE);
        return "E:\\gclass\\" + year + "\\" + (month + 1) + "\\" + date;
    }

    public static String getFileReceivePath() {
        return getCachePath() + "\\files";
    }

    public static String getImageReceivePath() {
        return getCachePath() + "\\images";
    }

    public static HTMLEditorKit getHTMLEditorKit() {
        var kit = new HTMLEditorKit();
        var style = kit.getStyleSheet();
        style.addRule("blockquote {\n" +
                "  background: #f9f9f9;\n" +
                "  border-left: 10px solid #cccccc;\n" +
                "  margin: 10px 10px;\n" +
                "  padding: 8px 10px;\n" +
                "}");
        style.addRule("a {color: #333333;}");
        style.addRule("code {\n" +
                "    overflow-wrap: break-word;\n" +
                "    word-wrap: break-word;\n" +
                "    padding: 2px 4px;\n" +
                "    color: #555555;\n" +
                "    background: #eeeeee;\n" +
                "    border-radius: 3px;\n" +
                "}");
        return kit;
    }

}
