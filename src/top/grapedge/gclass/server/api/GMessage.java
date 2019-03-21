package top.grapedge.gclass.server.api;

import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.Database;
import top.grapedge.gclass.server.base.GServerManager;
import top.grapedge.gclass.server.json.MessageJson;
import top.grapedge.gclass.server.json.UserJson;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description: 用于获取聊天的各种信息
 * @author: Grapes
 * @create: 2019-03-15 11:05
 **/
public class GMessage {
    public static Message getNewChat(int token) {
        try {
            var userid = GServerManager.getUserId(token);
            var sql = "SELECT sender FROM message WHERE receiver=? GROUP BY sender;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, userid);
            var rs = ps.executeQuery();
            List<UserJson> list = new ArrayList<>();
            while (rs.next()) {
                var u = GUser.getUserInfo(rs.getString("sender"));
                if (u != null)
                    list.add(u);
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean removeMessage(int msgid) {
        try {
            var sql = "DELETE FROM message WHERE msgid = ?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, msgid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<MessageJson> getNormalMessage(MessageJson messageJson) {
        try {
            var sql = "SELECT * FROM message WHERE type=0 AND receiver=? AND sender=? ORDER BY time;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, messageJson.receiver);
            ps.setString(2, messageJson.sender);
            var rs = ps.executeQuery();
            List<MessageJson> list = new ArrayList<>();
            while (rs.next()) {
                var msg = new MessageJson();
                msg.msgid = rs.getInt("msgid");
                msg.receiver = rs.getString("receiver");
                msg.sender = rs.getString("sender");
                msg.text = rs.getString("text");
                msg.time = rs.getTimestamp("time").getTime();
                msg.type = rs.getInt("type");
                list.add(msg);
                // 阅读过消息后就移除这条消息
                removeMessage(msg.msgid);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // 得到群组消息
    private static List<MessageJson> getClassMessage(int token, MessageJson messageJson) {
        try {
            var sql = "SELECT * FROM message WHERE type=1 AND receiver=? AND time > ? ORDER BY time;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, messageJson.receiver);
            ps.setTimestamp(2, GGroup.getLastVisitedTime(token, messageJson.receiver));

            GGroup.setLastVisitedTime(token, messageJson.receiver, new Timestamp(Calendar.getInstance().getTimeInMillis()));

            var rs = ps.executeQuery();
            List<MessageJson> list = new ArrayList<>();
            while (rs.next()) {
                var msg = new MessageJson();
                msg.msgid = rs.getInt("msgid");
                msg.receiver = rs.getString("receiver");
                msg.sender = rs.getString("sender");
                msg.text = rs.getString("text");
                msg.time = rs.getTimestamp("time").getTime();
                msg.type = rs.getInt("type");
                list.add(msg);
                // 群组消息不清数据库
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static Message getMessage(int token, MessageJson messageJson) {
        try {
            //if (!(userid.equals(messageJson.sender) || userid.equals(messageJson.receiver))) return null;
            if (messageJson.type == 0) {
                return new Message(0, 0, Parser.toJson(getNormalMessage(messageJson)));
            } else {
                return new Message(0, 0, Parser.toJson(getClassMessage(token, messageJson)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addMessage(MessageJson json) {
        try {

            var sql = "INSERT INTO message (sender, receiver, text, time, type) VALUES (?,?,?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, json.sender);
            ps.setString(2, json.receiver);
            ps.setString(3, json.text);
            ps.setTimestamp(4, new Timestamp(json.time));
            ps.setInt(5, json.type);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
