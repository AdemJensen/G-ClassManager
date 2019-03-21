package top.grapedge.gclass.server.api;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.base.Database;
import top.grapedge.gclass.server.base.GServerManager;
import top.grapedge.gclass.server.json.*;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-14 23:04
 **/
public class GGroup {
    public static Message getClassInfo(String classId) {
        try {
            var sql = "SELECT * FROM class WHERE classid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, classId);
            var rs = ps.executeQuery();
            var classJson = new ClassJson();
            if (rs.next()) {
                classJson.classid = rs.getString("classid");
                classJson.gradeIndex = rs.getInt("grade");
                classJson.classIndex = rs.getInt("class");
                classJson.avatar = rs.getInt("avatar");
                classJson.intro = rs.getString("intro");
            } else {
                return new Message(-1, 0, "班级不存在");
            }
            return new Message(0, 0, classJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "查询失败");
    }
    public static Message getMyClass(int token) {
        try {
            var userid = GServerManager.getUserId(token);
            var sql = "SELECT * FROM member WHERE userid=? GROUP BY classid;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, userid);
            var rs = ps.executeQuery();
            List<ClassJson> list = new ArrayList<>();
            while (rs.next()) {
                var c = getClassInfo(rs.getString("classid"));
                if (c != null)
                    list.add(ClassJson.parse(c.props));
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "查询失败");
    }

    public static Timestamp getLastVisitedTime(int token, String classId) {
        try {
            var userid = GServerManager.getUserId(token);
            var sql = "SELECT time FROM member WHERE userid=? AND classid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            System.out.println("用户“" + userid + " " + classId);
            ps.setString(1, userid);
            ps.setString(2, classId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static void setLastVisitedTime(int token, String classId, Timestamp date) {
        try {
            var userid = GServerManager.getUserId(token);
            var sql = "UPDATE member SET time=? WHERE userid=? AND classid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setTimestamp(1, date);
            ps.setString(2, userid);
            ps.setString(3, classId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<MemberJson> getMembers(String classId) {
        try {
            var sql = "SELECT * FROM member WHERE classid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, classId);
            var rs = ps.executeQuery();
            var list = new ArrayList<MemberJson>();
            while (rs.next()) {
                var member = new MemberJson();
                member.classId = rs.getString("classid");
                member.userId = rs.getString("userid");
                member.type = rs.getInt("type");
                list.add(member);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static MemberJson getMember(String userId, String classId) {
        try {
            var sql = "SELECT * FROM member WHERE classid=? AND userid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, classId);
            ps.setString(2, userId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                var member = new MemberJson();
                member.classId = rs.getString("classid");
                member.userId = rs.getString("userid");
                member.type = rs.getInt("type");
                return member;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message addClass(int token, ClassJson json) {
        var userid = GServerManager.getUserId(token);
        try {
            if (getClassInfo(json.classid) == null) {
                return new Message(-1, 0, "班级不存在");
            }
            var list = getMembers(json.classid);
            for (var i : list) {
                if (i.userId.equals(userid)) {
                    return new Message(-2, 0, "我已经在这个班级里啦！");
                }
            }
            insertMember(json.classid, userid, 2);
            return new Message(0, 0, "加入班级成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static void insertMember(String classid, String userid, int type) {
        try {
            var sql = "INSERT INTO member (classid, userid, type, time) VALUES (?,?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, classid);
            ps.setString(2, userid);
            ps.setInt(3, type);
            ps.setTimestamp(4, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Message updateMember(int token, String userid, String classid, int type) {
        Debug.log("设置管理员");
        try {
            var tmp = getMember(GServerManager.getUserId(token), classid);
            if (tmp == null || tmp.type != 0) {
                return new Message(-1, 0, "非法操作");
            }
            var json = getClassInfo(classid);
            if (json == null) {
                return new Message(-1, 0, "班级不存在");
            }
            var sql = "UPDATE member SET type=? WHERE userid=? AND classid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, type);
            ps.setString(2, userid);
            ps.setString(3, classid);
            ps.executeUpdate();
            return new Message(0, 0, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "错误");
    }

    public static Message createClass(int token, ClassJson json) {
        var userid = GServerManager.getUserId(token);
        try {
            // 虽然客户端已经确保，此处是为了确保非法输入
            if (String.valueOf(json.gradeIndex).length() != 4 || String.valueOf(json.classIndex).length() == 0 || String.valueOf(json.classIndex).length() > 4) {
                return new Message(-2, 0, "输入非法");
            }
            json.classid = String.format("%04d%04d", json.gradeIndex, json.classIndex);
            if (getClassInfo(json.classid) != null) {
                return new Message(-1, 0, "班级已存在");
            }
            var sql = "INSERT INTO class (classid, grade, class, avatar, intro) VALUES (?,?,?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, json.classid);
            ps.setInt(2, json.gradeIndex);
            ps.setInt(3, json.classIndex);
            ps.setInt(4, json.avatar);
            ps.setString(5, json.intro);
            ps.executeUpdate();
            insertMember(json.classid, userid, 0);
            return new Message(0, 0, "创建班级成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message getNoticeList(ClassJson json) {
        try {
            var sql = "SELECT * FROM notice WHERE classid=? ORDER BY time DESC";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, json.classid);
            var rs = ps.executeQuery();
            List<NoticeJson> list = new ArrayList<>();
            while (rs.next()) {
                var notice = new NoticeJson();
                notice.noticeId = rs.getInt("noticeid");
                notice.sender = rs.getString("sender");
                notice.text = rs.getString("text");
                notice.time = rs.getTimestamp("time").getTime();
                list.add(notice);
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");

    }

    public static Message addNotice(NoticeJson notice) {
        try {
            var sql = "INSERT INTO notice (sender,classid,text,time) VALUES (?,?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, notice.sender);
            ps.setString(2, notice.classId);
            ps.setString(3, notice.text);
            ps.setTimestamp(4, new Timestamp(notice.time));
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                notice.noticeId = rs.getInt(1);
            }
            return new Message(0, 0, notice.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message updateNotice(NoticeJson notice) {
        try {
            var sql = "UPDATE notice SET text=? WHERE noticeid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            Debug.log("更新：" + notice);
            ps.setString(1, notice.text);
            ps.setInt(2, notice.noticeId);
            ps.executeUpdate();
            return new Message(0, 0, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Message(-128, 0, "修改失败");
    }

    public static Message getNotice(NoticeJson notice) {
        try {
            var sql = "SELECT * FROM notice WHERE noticeid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, notice.noticeId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                var ntc = new NoticeJson();
                ntc.noticeId = rs.getInt("noticeid");
                ntc.sender = rs.getString("sender");
                ntc.text = rs.getString("text");
                ntc.time = rs.getTimestamp("time").getTime();
                return new Message(0, 0, ntc.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");

    }

    public static Message addVote(NoticeJson json) {
        try {
            var sql = "INSERT INTO vote (noticeid,classid,status,time) VALUES (?,?,0,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            String classId = json.classId;
            json.classId = "-1";
            var notice = NoticeJson.parse(addNotice(json).props);
            ps.setInt(1, notice.noticeId);
            ps.setString(2, classId);
            ps.setTimestamp(3, new Timestamp(notice.time));
            ps.executeUpdate();
            var vote = new VoteJson();
            vote.agree = 0;
            vote.disagree = 0;
            vote.status = 0;
            vote.noticeId = notice.noticeId;
            vote.classId = classId;
            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                notice.noticeId = rs.getInt(1);
            }
            return new Message(0, 0, vote.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message getVoteList(ClassJson json) {
        try {
            var sql = "SELECT * FROM vote WHERE classid=? ORDER BY time DESC";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, json.classid);
            var rs = ps.executeQuery();
            List<VoteJson> list = new ArrayList<>();
            while (rs.next()) {
                var vote = new VoteJson();
                vote.noticeId = rs.getInt("noticeid");
                vote.classId = rs.getString("classid");
                vote.voteId = rs.getInt("voteid");
                vote.time = rs.getTimestamp("time").getTime();
                vote.status = rs.getInt("status");
                vote.agree = rs.getInt("agree");
                vote.disagree = rs.getInt("disagree");
                list.add(vote);
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message getAdviceList(VoteJson voteJson) {
        try {
            var sql = "SELECT * FROM suggestion WHERE voteid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, voteJson.voteId);
            var rs = ps.executeQuery();
            List<MessageJson> list = new ArrayList<>();
            while (rs.next()) {
                var msg = new MessageJson();
                msg.text = rs.getString("text");
                msg.receiver = voteJson.noticeId + "";
                list.add(msg);
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message setVoteStatus(VoteJson vote) {
        try {
            var sql = "UPDATE vote SET status=? WHERE voteid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, vote.status);
            ps.setInt(2, vote.voteId);
            if (vote.status == 1) {
                new Thread(()-> {
                    try {
                        //          小时  分钟  秒    毫秒
                        Thread.sleep(24 * 60 * 60 * 1000);
                        // 一天之后结束投票
                        endVote(vote);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            ps.executeUpdate();
            return new Message(0, 0, "启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "修改失败");
    }


    private static void endVote(VoteJson vote) {
        vote.status = 2;
        setVoteStatus(vote);
        // 下面注释的内容是实现投票后通知，想了想还是不加了。
        /*var msg = new MessageJson();
        msg.type = 6;
        msg.receiver = vote.classId;
        msg.sender = "1";
        msg.text
        GChatClient.getInstance().sendMessage();
        */
    }

    public static void addVoteUser(String userid, int voteid) {
        try {
            var sql = "INSERT INTO whovote (voteid,userid) VALUES (?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, voteid);
            ps.setString(2, userid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isVote(String userid, int voteid) {
        try {
            var sql = "SELECT * FROM whovote WHERE voteid=? AND userid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, voteid);
            ps.setString(2, userid);
            var rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Message vote(VoteJson json) {
        try {
            if (isVote(json.member, json.voteId)) {
                return new Message(-1, 0, "你已经投过票啦");
            }
            var sql = "UPDATE vote SET agree=?, disagree=? WHERE voteid=?;";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            boolean agree = json.agree > 0;
            var vote = VoteJson.parse(getVoteInfo(json).props);
            ps.setInt(1, vote.agree + (agree ? 1 : 0));
            ps.setInt(2, vote.disagree + (agree ? 0 : 1));
            ps.setInt(3, vote.voteId);

            if (getMembers(json.classId).size() == vote.agree + vote.disagree + 1) {
                endVote(vote);
            }
            ps.executeUpdate();
            addVoteUser(json.member, json.voteId);
            return new Message(0, 0, "投票成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "修改失败");
    }

    public static Message getVoteInfo(VoteJson json) {
        try {
            var sql = "SELECT * FROM vote WHERE voteid=?";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setInt(1, json.voteId);
            var rs = ps.executeQuery();
            var vote = new VoteJson();
            if (rs.next()) {
                vote.noticeId = rs.getInt("noticeid");
                vote.classId = rs.getString("classid");
                vote.voteId = rs.getInt("voteid");
                vote.time = rs.getTimestamp("time").getTime();
                vote.status = rs.getInt("status");
                vote.agree = rs.getInt("agree");
                vote.disagree = rs.getInt("disagree");
                return new Message(0, 0, vote.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message addAdvice(NoticeJson parse) {
        try {
            var sql = "INSERT INTO suggestion (voteid,text) VALUES (?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, parse.noticeId);
            ps.setString(2, parse.text);
            ps.executeUpdate();
            return new Message(0, 0, "加入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-1, 0, "加入失败");
    }

    public static Message getFileList(ClassJson json) {
        try {
            var sql = "SELECT * FROM cfile WHERE classid=? ORDER BY time DESC";
            var ps = Database.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, json.classid);
            var rs = ps.executeQuery();
            List<FileJson> list = new ArrayList<>();
            while (rs.next()) {
                var file = new FileJson();
                file.fileId = rs.getInt("fileid");
                var msg = GFile.getFile(file);
                if (msg != null) {
                    file = FileJson.parse(msg.props);
                    list.add(file);
                }
            }
            return new Message(0, 0, Parser.toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "出现未知错误");
    }

    public static Message addFile(FileJson file) {
        try {
            var sql = "INSERT INTO cfile (fileid,classid,time) VALUES (?,?,?);";
            var ps = Database.getInstance().getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, file.fileId);
            ps.setString(2, file.classId);
            ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.executeUpdate();
            return new Message(0, 0, "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "添加文件失败");
    }
}
