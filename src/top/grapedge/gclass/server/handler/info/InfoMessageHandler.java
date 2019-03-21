package top.grapedge.gclass.server.handler.info;

import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.base.net.Message;
import top.grapedge.gclass.server.api.GGroup;
import top.grapedge.gclass.server.api.GMessage;
import top.grapedge.gclass.server.api.GUser;
import top.grapedge.gclass.server.base.MessageHandler;
import top.grapedge.gclass.server.json.*;

/**
 * @program: G-ClassManager
 * @description: 处理信息获取
 * @author: Grapes
 * @create: 2019-03-14 12:51
 **/
public class InfoMessageHandler extends MessageHandler {

    private int token = Integer.MIN_VALUE;

    public void logout() {
        GUser.logout(token);
    }
    @Override
    public Message handleMessage(Message message) {
        Message result = null;
        switch (message.code) {
            // 登录
            case GClass.LOGIN:
                result = GUser.login(UserJson.parse(message.props));
                token = result.token;
                break;
            // 注册
            case GClass.REGISTER:
                result = GUser.register(UserJson.parse(message.props));
                break;
            // 获取用户信息
            case GClass.USER_INFO:
                var user = GUser.getUserInfo(UserJson.parse(message.props).userid);
                result = new Message(0, 0, user == null ? "" : user.toString());
                break;
            // 获取班级信息
            case GClass.CLASS_INFO:
                result = GGroup.getClassInfo(ClassJson.parse(message.props).classid);
                break;
            // 获取用户聊天内容
            case GClass.CHAT_MSG:
                result = GMessage.getMessage(token, MessageJson.parse(message.props));
                break;
            case GClass.MY_CLASS:
                result = GGroup.getMyClass(token);
                break;
            case GClass.MY_CHAT:
                result = GMessage.getNewChat(token);
                break;
            case GClass.MEMBERS:
                result = new Message(0, 0, Parser.toJson(GGroup.getMembers(ClassJson.parse(message.props).classid)));
                break;
            case GClass.ADD_CLASS:
                result = GGroup.addClass(message.token, ClassJson.parse(message.props));
                break;
            case GClass.CREATE_CLASS:
                result = GGroup.createClass(message.token, ClassJson.parse(message.props));
                break;
            case GClass.SET_ADMIN:
                var member = MemberJson.parse(message.props);
                result = GGroup.updateMember(token, member.userId, member.classId, member.type);
                break;
            case GClass.NOTICE_LIST:
                result = GGroup.getNoticeList(ClassJson.parse(message.props));
                break;
            case GClass.ADD_NOTICE:
                result = GGroup.addNotice(NoticeJson.parse(message.props));
                break;
            case GClass.UPDATE_NOTICE:
                result = GGroup.updateNotice(NoticeJson.parse(message.props));
                break;
            case GClass.NOTICE_INFO:
                result = GGroup.getNotice(NoticeJson.parse(message.props));
                break;
            case GClass.ADD_VOTE:
                result = GGroup.addVote(NoticeJson.parse(message.props));
                break;
            case GClass.VOTE_LIST:
                result = GGroup.getVoteList(ClassJson.parse(message.props));
                break;
            case GClass.ADVICE_LIST:
                result = GGroup.getAdviceList(VoteJson.parse(message.props));
                break;
            case GClass.SET_VOTE:
                result = GGroup.setVoteStatus(VoteJson.parse(message.props));
                break;
            case GClass.VOTE:
                result = GGroup.vote(VoteJson.parse(message.props));
                break;
            case GClass.ADD_ADVICE:
                result = GGroup.addAdvice(NoticeJson.parse(message.props));
                break;
            case GClass.FILE_LIST:
                result = GGroup.getFileList(ClassJson.parse(message.props));
                break;
            case GClass.ADD_FILE:
                result = GGroup.addFile(FileJson.parse(message.props));
                break;
        }
        return result;
    }
}
