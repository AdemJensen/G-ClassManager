package top.grapedge.gclass.client.base;

import com.google.gson.reflect.TypeToken;
import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.base.net.GSocket;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.json.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description: 建立与服务器的连接并获取信息，返回结果大于等于0为成功，否则为失败
 * @author: Grapes
 * @create: 2019-03-14 14:10
 **/
public class GPost {
    private int token = Integer.MAX_VALUE;
    private static GPost instance;
    private GSocket socket;

    public static GPost getInstance() {
        return instance;
    }

    public GPost() throws IOException {
        instance = this;
        socket = new GSocket(new Socket(GClass.HOST, GClass.INFO_PORT));
    }

    private synchronized Message post(Message message) {
        try {
            socket.writeUTF(message.toString());
            return Message.parse(socket.readUTF());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message(-128, 0, "空结果错误");
    }

    /**
     * 登录功能
     * @param user 用户信息
     * @return 登录结果
     * @throws IOException
     */
    public synchronized Message login(UserJson user) {
        var result = post(new Message(GClass.LOGIN, token, user.toString()));
        if (result.code >= 0) token = result.token;
        return result;
    }

    public synchronized Message register(UserJson user) {
        return post(new Message(GClass.REGISTER, token, user.toString()));
    }

    public synchronized UserJson getUserInfo(String userid) {
        return getUserInfo(new UserJson(userid));
    }

    public synchronized UserJson getUserInfo(UserJson user) {
        var result = post(new Message(GClass.USER_INFO, token, user.toString()));
        return result.code < 0 ? null : UserJson.parse(result.props);
    }

    public synchronized int getAvatarId(String userid, boolean isClass) {
        return isClass ? getClassInfo(new ClassJson(userid)).avatar : getUserInfo(new UserJson(userid)).avatar;
    }

    public synchronized List<ClassJson> getMyClass() {
        var result = post(new Message(GClass.MY_CLASS, token, ""));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<ClassJson>>() {}.getType());
    }

    public synchronized List<UserJson> getChatUser() {
        var result = post(new Message(GClass.MY_CHAT, token, ""));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<UserJson>>() {}.getType());
    }

    public synchronized List<MessageJson> getMessage(MessageJson info) {
        var result = post(new Message(GClass.CHAT_MSG, token, info.toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<MessageJson>>() {}.getType());
    }

    public synchronized ClassJson getClassInfo(ClassJson classJson) {
        var result = post(new Message(GClass.CLASS_INFO, token, classJson.toString()));
        return result.code < 0 ? null : ClassJson.parse(result.props);
    }

    public synchronized List<MemberJson> getMembers(ClassJson classJson) {
        var result = post(new Message(GClass.MEMBERS, token, classJson.toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<MemberJson>>() {}.getType());
    }

    /**
     * 创建班级
     * @param json 需要设置id，年级，班级
     * @return 结果
     */
    public synchronized Message createClass(ClassJson json) {
        return post(new Message(GClass.CREATE_CLASS, token, json.toString()));
    }

    /**
     * 加入班级
     * @param json 需要设置班级号
     * @return 结果
     */
    public synchronized Message addClass(ClassJson json) {
        return post(new Message(GClass.ADD_CLASS, token, json.toString()));
    }

    public synchronized Message setAdmin(MemberJson member) {
        return post(new Message(GClass.SET_ADMIN, token, member.toString()));
    }

    public List<NoticeJson> getNoticeList(String classId) {
        var result = post(new Message(GClass.NOTICE_LIST, token, new ClassJson(classId).toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<NoticeJson>>() {}.getType());
    }

    public synchronized Message publishNotice(NoticeJson notice) {
        return post(new Message(GClass.ADD_NOTICE, token, notice.toString()));
    }

    public synchronized NoticeJson getNotice(int noticeId) {
        var result = post(new Message(GClass.NOTICE_INFO, token, new NoticeJson(noticeId).toString()));
        return result.code < 0 ? null : NoticeJson.parse(result.props);
    }

    public synchronized Message publishVote(NoticeJson notice) {
        return post(new Message(GClass.ADD_VOTE, token, notice.toString()));
    }

    public synchronized List<VoteJson> getVoteList(String classId) {
        var result = post(new Message(GClass.VOTE_LIST, token, new ClassJson(classId).toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<VoteJson>>() {}.getType());
    }

    public synchronized List<MessageJson> getAdviceList(VoteJson vote) {
        var result = post(new Message(GClass.ADVICE_LIST, token, vote.toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<MessageJson>>() {}.getType());
    }

    public synchronized Message updateNotice(NoticeJson notice) {
        return post(new Message(GClass.UPDATE_NOTICE, token, notice.toString()));
    }

    public synchronized Message startVote(VoteJson vote) {
        vote.status = 1;
        return post(new Message(GClass.SET_VOTE, token, vote.toString()));
    }

    public synchronized Message addVoteAdvice(String sug, int voteId) {
        var msg = new NoticeJson();
        msg.noticeId = voteId;
        msg.text = sug;
        return post(new Message(GClass.ADD_ADVICE, token, msg.toString()));
    }

    public synchronized Message vote(String userid, int voteId, String classId, int agree) {
        var vote = new VoteJson();
        vote.voteId = voteId;
        vote.classId = classId;
        vote.member = userid;
        if (agree == 1) {
            vote.agree = 1;
        } else {
            vote.disagree = 1;
        }
        return post(new Message(GClass.VOTE, token, vote.toString()));
    }

    public synchronized List<FileJson> getFileList(ClassJson json) {
        var result = post(new Message(GClass.FILE_LIST, token, json.toString()));
        if (result.code < 0) return new ArrayList<>();
        return Parser.fromJson(result.props, new TypeToken<List<FileJson>>() {}.getType());
    }

    public synchronized void addFile(FileJson fileJson) {
        post(new Message(GClass.ADD_FILE, token, fileJson.toString()));
    }
}
