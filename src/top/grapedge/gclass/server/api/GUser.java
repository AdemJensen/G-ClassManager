package top.grapedge.gclass.server.api;

import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.Database;
import top.grapedge.gclass.server.base.GServerManager;
import top.grapedge.gclass.server.json.UserJson;

import java.sql.SQLException;

/**
 * @program: G-ClassManager
 * @description: 登录API
 * @author: Grapes
 * @create: 2019-03-14 13:41
 **/
public class GUser {
    public static Message login(UserJson user) {
        try {
            // 先判断用户名和密码是否和数据库相同
            if (checkAccount(user)) {
                // 设置登录状态为登录
                setUserStatus(user.userid, 1);
                // 生成token
                var token = 0;
                while (GServerManager.containsToken(token)) {
                    token = (int)(Math.random() * 1e5);
                }
                GServerManager.addToken(token, user.userid);
                return new Message(0, token, "登录成功");
            } else {
                return new Message(-1, Integer.MIN_VALUE, "账号或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkAccount(UserJson user) throws SQLException {
        var sql = "SELECT userid FROM user WHERE userid=? AND password=?";
        var ps = Database.getInstance().getConn().prepareStatement(sql);
        ps.setString(1, user.userid);
        ps.setString(2, user.password);
        var rs = ps.executeQuery();
        // 如果查询到对应用户就登录
        return rs.next();
    }

    private static void setUserStatus(String userid, int status) {
        try {
            var updateStatus = "UPDATE user SET status=?, last_online=? WHERE userid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(updateStatus);
            ps.setInt(1, status);
            ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setString(3, userid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void logout(int token) {
        var userid = GServerManager.getUserId(token);
        // 设置用户状态为离线
        setUserStatus(userid, 0);
    }

    /**
     * 获得用户登录状态
     * @param token
     * @return
     */
    public static int getStatus(int token) {
        var userid = GServerManager.getUserId(token);
        try {
            var updateStatus = "SELECT status FROM user WHERE userid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(updateStatus);
            ps.setString(1, userid);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 获得用户信息
     * @param userid
     * @return
     */
    public static UserJson getUserInfo(String userid) {
        try {
            var updateStatus = "SELECT * FROM user WHERE userid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(updateStatus);
            ps.setString(1, userid);
            var rs = ps.executeQuery();
            if (rs.next()) {
                var user = new UserJson();
                user.userid = userid;
                user.password = "就不告诉你";
                user.avatar = rs.getInt("avatar");
                user.username = rs.getString("username");
                user.status = rs.getInt("status");
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message register(UserJson user) {
        try {
            if(getUserInfo(user.userid) != null) {
                return new Message(-1, 0, "学号已存在");
            } else if (user.userid.length() != 12) {
                return new Message(-2, 0, "学号错误");
            }
            var sql = "INSERT INTO user (userid, password, username, avatar, status) VALUES (?,?,?,?,0);";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, user.userid);
            ps.setString(2, user.password);
            ps.setString(3, user.username);
            ps.setInt(4, user.avatar);
            ps.executeUpdate();
            return new Message(0, 0, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
