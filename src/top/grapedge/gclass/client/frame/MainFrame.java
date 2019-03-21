package top.grapedge.gclass.client.frame;


import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.base.MarkDownParser;
import top.grapedge.gclass.client.base.*;
import top.grapedge.gclass.client.ui.ChatBar;
import top.grapedge.gclass.server.json.*;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.base.util.ImageUtil;
import top.grapedge.ui.component.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @program: G-ClassManager
 * @description: 主框架
 * @author: Grapes
 * @create: 2019-03-07 20:11
 **/
public class MainFrame extends GFrame implements ActionListener, GListener {
    private int width = 1152;
    private int height = 648;
    private int titleBarHeight = 65;
    private int paneHeight = height - titleBarHeight;
    private int groupWidth = 300;
    private int paneWidth = width - groupWidth;
    private int chatTitleBarHeight = 50;
    private int chatMessageHeight = 160;
    private int chatHeight = paneHeight - chatMessageHeight - chatTitleBarHeight;
    private int msgToolBarHeight = 30;
    private int msgSendHeight = 35;
    private int msgTextAreaHeight = chatHeight - msgSendHeight - msgToolBarHeight;
    private int chatBarHeight = 60;
    private int groupHeight = paneHeight - chatBarHeight;
    private int avatarSize = 45;
    private int toolSize = 150;

    private int noticeWidth = 600;
    private int noticeHeight = 350;

    private final MainStatus mainStatus;

    // 提升变量区域
    private GTextPane msgInput;

    MainFrame(String userid, int token) {
        mainStatus = new MainStatus();
        mainStatus.setUserId(userid);
        mainStatus.setToken(token);

        setSize(width, height);
        Debug.out("基本框架加载");
        // 绘制基本框架
        initView();
        Debug.out("获取用户头像");
        // 获取当前用户头像
        setMyAvatar();
        Debug.out("班级列表加载");
        // 获取我的班级
        getMyClass();
        Debug.out("获取新消息");
        // 获取有新消息的列表
        getNewChatBar();
        Debug.out("读取聊天记录");
        // 读取本地聊天记录
        addOldChat();
        Debug.out("聊天引擎加载");
        GChatClient.getInstance().register(mainStatus.getToken());
        GChatClient.getInstance().addMsgListener(this);
        Debug.out("加载完成.");
        // 绘制聊天条
        showFrame();
    }

    private void showFrame() {
        repaintChatBar();
        setResizable(false);
        setUndecorated(true);
        setBackground(ColorUtil.TRANSPARENT);
        setMinimumSize(new Dimension(width, height));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // 用于切换公告、聊天、投票、文件
    private GPanel cardPane = new GPanel(new CardLayout());
    private final String CHAT_CARD = "CHAT";
    private final String VOTE_CARD = "VOTE";
    private final String NOTICE_CARD = "NOTICE";
    private final String FILE_CARD = "FILE";

    private void switchCardPane(String target) {
        ((CardLayout)cardPane.getLayout()).show(cardPane, target);
    }

    /**
     * 创建标题栏
     */
    private void createTitleBar() {
        // 标题栏面板
        GPanel northPane = new GPanel();
        northPane.setLayout(new BorderLayout());
        addDragMoveFunction(northPane);
        //(1(FlowLayout)northPane.getLayout()).setAlignment(FlowLayout.LEFT);
        northPane.setBackground(new Color(0x333333));
        northPane.setPreferredSize(new Dimension(width, titleBarHeight));
        add(northPane, BorderLayout.NORTH);

        // 个人头像
        GPanel avatarPane = new GPanel();
        avatarPane.setLayout(new BoxLayout(avatarPane, BoxLayout.X_AXIS));
        avatarPane.setPreferredSize(new Dimension(toolSize, titleBarHeight));

        avatarImage = new GImage(new ImageIcon("images/avatar.jpg"));
        avatarImage.setRound();
        avatarImage.setPreferredSize(new Dimension(avatarSize, avatarSize));

        avatarPane.setBorder(new EmptyBorder(0, (toolSize - avatarSize) / 4, 0, 0));
        avatarPane.setBackground(new Color(0x333333));
        avatarPane.add(avatarImage);

        northPane.add(avatarPane, BorderLayout.WEST);

        // 工具栏面板
        GPanel toolPane = new GPanel();
        toolPane.setLayout(new BoxLayout(toolPane, BoxLayout.X_AXIS));
        toolPane.setBackground(new Color(0x333333));
        toolPane.setPreferredSize(new Dimension(toolSize, titleBarHeight));
        northPane.add(toolPane, BorderLayout.EAST);
        // 工具栏按钮
        GButton tools = new GButton();
        ImageIcon toolsIcon = new ImageIcon("images/tools.png");
        toolsIcon.setImage(toolsIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        tools.setIcon(toolsIcon);
        tools.setRadius(7);
        tools.setPreferredSize(new Dimension(30, 30));
        tools.setNormalColor(ColorUtil.TRANSPARENT);
        tools.addActionListener(e-> showTools(tools, tools.getX(), tools.getY() + 30));
        toolPane.add(tools);
        // 最小化按钮
        GButton minus = new GButton();
        ImageIcon minusIcon = new ImageIcon("images/minus.png");
        minusIcon.setImage(minusIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        minus.setIcon(minusIcon);
        minus.setRadius(7);
        minus.setPreferredSize(new Dimension(30, 30));
        minus.setNormalColor(ColorUtil.TRANSPARENT);
        minus.addActionListener(e-> setExtendedState(JFrame.ICONIFIED));
        toolPane.add(minus);
        // 退出按钮
        GButton close = new GButton();
        ImageIcon closeIcon = new ImageIcon("images/close.png");
        closeIcon.setImage(closeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        close.setIcon(closeIcon);
        close.setRadius(7);
        close.setPreferredSize(new Dimension(30, 30));
        close.setNormalColor(ColorUtil.TRANSPARENT);
        close.addActionListener(e-> quit());
        toolPane.add(close);
    }

    private GLabel chatTitle;
    private GPanel chatTool;
    private void createChatPane() {
        // 聊天区域面板
        GPanel centerPane = new GPanel(new BorderLayout());
        centerPane.setBackground(new Color(0xF2F3F4));
        centerPane.setPreferredSize(new Dimension(paneWidth, paneHeight));
        add(centerPane, BorderLayout.CENTER);
        // 标题栏
        GPanel chatTitleBar = new GPanel(new BorderLayout());
        chatTitleBar.setBackground(new Color(0xFDFEFE));
        chatTitleBar.setPreferredSize(new Dimension(paneWidth, chatTitleBarHeight));
        chatTitleBar.setBorder(BorderFactory.createCompoundBorder(chatTitleBar.getBorder(),
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xD0D3D4))));
        chatTitle = new GLabel("欢迎使用Crapes");
        chatTitle.setFont(GConfig.createFont(18));
        chatTitle.setBorder(BorderFactory.createCompoundBorder(chatTitle.getBorder(),
                BorderFactory.createEmptyBorder(0, 25, 0, 0)));     // Margin
        chatTitleBar.add(chatTitle, BorderLayout.WEST);

        // 聊天工具
        chatTool = new GPanel();
        chatTool.setLayout(new BoxLayout(chatTool, BoxLayout.X_AXIS));
        chatTool.setBackground(new Color(0xFDFEFE));
        chatTool.setPreferredSize(new Dimension(230, chatTitleBarHeight));
        // 聊天标签
        // 聊天标签栏
        GLabel chatLabel = new GLabel(" 聊天 ");
        chatLabel.setFont(GConfig.createFont(15));
        chatLabel.addActionListener(this);
        chatTool.add(chatLabel);
        // 公告标签
        GLabel noticeLabel = new GLabel(" 公告 ");
        noticeLabel.setFont(GConfig.createFont(15));
        noticeLabel.addActionListener(this);
        chatTool.add(noticeLabel);
        // 文件标签
        GLabel fileLabel = new GLabel(" 文件 ");
        fileLabel.setFont(GConfig.createFont(15));
        fileLabel.addActionListener(this);
        chatTool.add(fileLabel);
        // 投票标签
        GLabel voteLabel = new GLabel(" 投票 ");
        voteLabel.setFont(GConfig.createFont(15));
        voteLabel.addActionListener(this);
        chatTool.add(voteLabel);

        // 投票标签
        GLabel memberLabel = new GLabel(" 成员 ");
        memberLabel.setFont(GConfig.createFont(15));
        memberLabel.addActionListener(this);
        chatTool.add(memberLabel);

        chatTool.setVisible(false);
        chatTitleBar.add(chatTool, BorderLayout.EAST);
        centerPane.add(chatTitleBar, BorderLayout.NORTH);
        centerPane.add(cardPane, BorderLayout.CENTER);
        // 以下区域分为若干个标签栏
        createChatCard();
        createNoticeCard();
        createVoteCard();
        createFileCard();
        cardPane.setVisible(false);
    }

    private GPanel chatPane;

    /**
     * 添加聊天气泡到面板
     * @param bubble 要添加的气泡
     * @param avatarId 头像id
     * @param sender
     */
    private void addChatBubble(GChatBubble bubble, int avatarId, String sender) {
        var avatar = new GImage(getAvatar(avatarId));
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setRound();
        avatar.setToolTipText(sender);
        GPanel avatarPane = new GPanel();
        avatarPane.add(avatar);
        avatarPane.setOpaque(false);
        GPanel panel = new GPanel(new FlowLayout(bubble.isLeft() ? FlowLayout.LEFT : FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        panel.setOpaque(false);

        if (bubble.getPreferredSize().width > paneWidth * 0.65) {
            bubble.setPreferredSize((int) (paneWidth * 0.65));
        }
        if (bubble.isLeft()) panel.add(avatarPane);
        panel.add(bubble);
        if (!bubble.isLeft()) panel.add(avatarPane);
        avatarPane.setPreferredSize(new Dimension(avatarPane.getPreferredSize().width, bubble.getPreferredSize().height));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, panel.getPreferredSize().height));
        chatPane.add(panel);
    }

    /**
     * 删除所有的气泡
     */
    private void removeChatBubble() {
        chatPane.removeAll();
        chatPane.revalidate();
        chatPane.repaint();
    }


    private void createChatCard() {
        GPanel chatCard = new GPanel(new BorderLayout());
        // 聊天区域
        chatPane = new GPanel();
        chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));
        //chatPane.setPreferredSize(new Dimension(paneWidth, chatHeight));

        chatPane.setBackground(new Color(0xFDFEFE));
        //chatPane.setBackground(Color.YELLOW);

        var chatScrollPane = new GScrollPane(chatPane);
        chatScrollPane.setBorder(null);
        chatScrollPane.setPreferredSize(new Dimension(paneWidth, chatHeight));
        chatScrollPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        chatCard.add(chatScrollPane, BorderLayout.CENTER);

        // 发送消息区域
        GPanel msgPane = new GPanel(new BorderLayout());
        msgPane.setBackground(new Color(0xFDFEFE));
        msgPane.setPreferredSize(new Dimension(paneWidth, chatMessageHeight));
        chatCard.add(msgPane, BorderLayout.SOUTH);
        // 发送消息工具栏
        GPanel toolBar = new GPanel(new FlowLayout(FlowLayout.LEFT));
        ((FlowLayout)toolBar.getLayout()).setHgap(10);
        toolBar.setPreferredSize(new Dimension(paneWidth, msgToolBarHeight));
        toolBar.setBackground(new Color(0xFDFEFE));
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        toolBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xD0D3D4)), toolBar.getBorder()));
        // 插入图片按钮
        GImage picture = new GImage(new ImageIcon("images/picture.png"));
        picture.setPreferredSize(new Dimension(20, 20));
        picture.addActionListener(e-> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("选择一张png或jpg图片", "jpg", "png");
            fileChooser.setFileFilter(filter);
            int rtn = fileChooser.showOpenDialog(null);
            if(rtn == JFileChooser.APPROVE_OPTION) {
                var filePath = fileChooser.getSelectedFile().getAbsolutePath();
                ImageIcon img = new ImageIcon(filePath);
                ImageUtil.scaleImage(img, 80);
                msgInput.insertIcon(img);
            }
        });
        toolBar.add(picture);
        // 画笔按钮
        GImage paint = new GImage(new ImageIcon("images/paint.png"));
        paint.setPreferredSize(new Dimension(20, 20));
        paint.addActionListener(e-> new GMessageDialog(this, "画笔功能暂未开放", "来ala"));
        toolBar.add(paint);
        ////////////////////////////////
        msgPane.add(toolBar, BorderLayout.NORTH);
        // 发送消息文本区域
        msgInput = new GTextPane();

        msgInput.setFont(GConfig.createFont(15));
        msgInput.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 12));

        GScrollPane msgScrollPane = new GScrollPane(msgInput);

        msgScrollPane.setBorder(null);
        msgScrollPane.setPreferredSize(new Dimension(paneWidth, msgTextAreaHeight));
        msgScrollPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        msgScrollPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        msgScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        toolBar.setBackground(new Color(0xFDFEFE));
        msgPane.add(msgScrollPane, BorderLayout.CENTER);
        // 发送按钮

        GPanel sendPane = new GPanel(new BorderLayout());
        GButton send = new GButton("发送");
        // 发送功能
        send.addActionListener(e-> {
            sendMessage(msgInput.getMessage());
            msgInput.setText("");
        });
        send.setPreferredSize(new Dimension(100, 30));
        sendPane.setBackground(new Color(0xFDFEFE));
        sendPane.add(send, BorderLayout.EAST);
        sendPane.setBorder(new EmptyBorder(8, 0, 8, 8));
        msgPane.add(sendPane, BorderLayout.SOUTH);
        cardPane.add(chatCard, CHAT_CARD);
    }

    private GPanel noticeCard;

    private MessageJson createMessageJson(int type, String text) {
        var ntMsg = new MessageJson();
        ntMsg.type = type;
        ntMsg.sender = mainStatus.getUserId();
        ntMsg.text = text;
        ntMsg.receiver = mainStatus.getTargetId();
        ntMsg.time = Calendar.getInstance().getTimeInMillis();
        return ntMsg;
    }
    private void createNoticeCard() {
        var publish = new GButton("发布公告");
        var publishPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        publishPane.setBackground(new Color(0xECF0F1));
        publish.addActionListener(e-> {
            // 判断是否是管理员
            if (mainStatus.isAdmin()) {
                var editor = new ContentEditor("发布公告", this);
                setEnabled(false);
                editor.setEditorListener(text-> {
                    var notice = new NoticeJson();
                    notice.sender = mainStatus.getUserId();
                    notice.time = Calendar.getInstance().getTimeInMillis();
                    notice.text = text;
                    notice.classId = mainStatus.getTargetId();
                    var message = GPost.getInstance().publishNotice(notice);
                    if (message.code >= 0) {
                        setEnabled(true);
                        new GMessageDialog(editor, "发布成功", "好啦");
                        var ntMsg = createMessageJson(3, "快来看公告啦！");
                        GChatClient.getInstance().sendMessage(ntMsg);
                        editor.dispose();
                        updateNotice(mainStatus.getTargetId());
                    } else {
                        new GMessageDialog(editor, message.props, "失败");
                    }
                });
            } else {
                new GMessageDialog(this, "只有管理员可以发布公告，您可以向班长进行申请。", "我不能那么做");
            }
        });

        publishPane.add(publish);
        var pane = new GPanel(new BorderLayout());
        noticeCard = new GPanel();
        noticeCard.setBackground(new Color(0xF4F6F7));
        noticeCard.setLayout(new BoxLayout(noticeCard, BoxLayout.Y_AXIS));
        var scrollPane = new GScrollPane(noticeCard);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.setBackground(new Color(0xF4F6F7));
        pane.add(scrollPane, BorderLayout.CENTER);
        pane.add(publishPane, BorderLayout.NORTH);
        cardPane.add(pane, NOTICE_CARD);
    }

    private void updateNotice(String classId) {
        var list = GPost.getInstance().getNoticeList(classId);
        if (list == null) return;
        noticeCard.removeAll();
        for (var i : list) {
            var pane = createNotice(i);
            noticeCard.add(pane);
        }
        noticeCard.revalidate();
        noticeCard.repaint();
    }

    private GPanel createEditorPane(String html) {
        var pane = new GPanel();
        pane.setBackground(new Color(0xF4F6F7));
        var webview = new GWebView();
        var webScrollPane = new GScrollPane(webview);
        webScrollPane.setPreferredSize(new Dimension(noticeWidth, noticeHeight));
        webScrollPane.setBorder(null);
        webScrollPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        webScrollPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        webScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        webScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        webScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        webview.setText(html);
        pane.setBorder(null);
        pane.add(webScrollPane);
        return pane;
    }

    private GPanel createNotice(NoticeJson notice) {
        var pane = new GPanel(new BorderLayout());
        pane.setPreferredSize(new Dimension(paneWidth, noticeHeight + 30));
        pane.setMaximumSize(new Dimension(paneWidth, noticeHeight + 30));
        pane.add(createEditorPane(new MarkDownParser().parse(notice.text)), BorderLayout.CENTER);
        var infoPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        var sender = GPost.getInstance().getUserInfo(notice.sender);
        infoPane.add(new GLabel("发布人：" + sender.username));
        infoPane.add(new GLabel("  时间：" + LocalDateTime.ofInstant(Instant.ofEpochMilli(notice.time),
                TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) + "  "));
        infoPane.setBackground(new Color(0xF0F3F4));
        infoPane.setBorder(new EmptyBorder(0, 120, 0, 120));
        pane.add(infoPane, BorderLayout.SOUTH);
        return pane;
    }

    private GPanel voteCard;
    private void createVoteCard() {
        var publish = new GButton("发布文稿");
        var publishPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        publishPane.setBackground(new Color(0xECF0F1));
        publish.addActionListener(e-> {
            // 判断是否是管理员
            if (mainStatus.isAdmin()) {
                var editor = new ContentEditor("发布文稿", this);
                setEnabled(false);
                editor.setEditorListener(text-> {
                    var notice = new NoticeJson();
                    notice.sender = mainStatus.getUserId();
                    notice.time = Calendar.getInstance().getTimeInMillis();
                    notice.text = text;
                    notice.classId = mainStatus.getTargetId();
                    var message = GPost.getInstance().publishVote(notice);
                    if (message.code >= 0) {
                        setEnabled(true);
                        new GMessageDialog(editor, "发布成功", "好啦");
                        var ntMsg = createMessageJson(4, "审议文稿开始。");
                        GChatClient.getInstance().sendMessage(ntMsg);
                        editor.dispose();
                        updateVote(mainStatus.getTargetId());
                    } else {
                        new GMessageDialog(editor, message.props, "失败");
                    }
                });
            } else {
                new GMessageDialog(this, "只有管理员可以发布文稿，您可以向班长进行申请。", "我不能那么做");
            }
        });

        publishPane.add(publish);

        var pane = new GPanel(new BorderLayout());
        voteCard = new GPanel();
        voteCard.setBackground(new Color(0xF4F6F7));
        voteCard.setLayout(new BoxLayout(voteCard, BoxLayout.Y_AXIS));
        var scrollPane = new GScrollPane(voteCard);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.setBackground(new Color(0xF4F6F7));
        pane.add(scrollPane, BorderLayout.CENTER);
        pane.add(publishPane, BorderLayout.NORTH);
        cardPane.add(pane, VOTE_CARD);
    }

    private void updateVote(String classId) {
        var list = GPost.getInstance().getVoteList(classId);
        if (list == null) return;
        voteCard.removeAll();
        for (var i : list) {
            var pane = createVote(i);
            voteCard.add(pane);
        }
        voteCard.revalidate();
        voteCard.repaint();
    }

    private GPanel createVote(VoteJson vote) {
        var pane = new GPanel(new BorderLayout());
        pane.setPreferredSize(new Dimension(paneWidth, noticeHeight + 75));
        pane.setMaximumSize(new Dimension(paneWidth, noticeHeight + 75));
        var notice = GPost.getInstance().getNotice(vote.noticeId);
        var noticePane = createNotice(notice);
        pane.add(noticePane, BorderLayout.CENTER);

        var operator = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        operator.setBorder(new EmptyBorder(0, 120, 0, 120));
        operator.setBackground(new Color(0xF0F3F4));
        switch (vote.status) {
            case 0:
                // 刚刚发布，等待审议
                if (notice.sender.equals(mainStatus.getUserId())) {
                    var update = new GButton("修改");
                    update.addActionListener(e-> {
                        var sugs = GPost.getInstance().getAdviceList(vote);
                        var editor = new ContentEditor("修改并开始投票", this, sugs);
                        editor.setText(notice.text);
                        setEnabled(false);
                        editor.setEditorListener(text-> {
                            var nt = new NoticeJson();
                            nt.sender = mainStatus.getUserId();
                            nt.text = text;
                            nt.classId = "-1";
                            nt.noticeId = vote.noticeId;
                            var message = GPost.getInstance().updateNotice(nt);
                            if (message.code >= 0) {
                                setEnabled(true);
                                GPost.getInstance().updateNotice(nt);
                                GPost.getInstance().startVote(vote);
                                new GMessageDialog(editor, "修改成功", "好啦");
                                var ntMsg = createMessageJson(5, "文稿修订完成，开始投票。");
                                GChatClient.getInstance().sendMessage(ntMsg);
                                editor.dispose();
                                updateVote(mainStatus.getTargetId());
                            } else {
                                new GMessageDialog(editor, message.props, "失败");
                            }
                        });
                    });
                    operator.add(update);
                } else {
                    var advice = new GButton("建议");
                    advice.addActionListener(e-> {
                        var sug = JOptionPane.showInputDialog("输入您的建议（匿名）");
                        if (sug != null) {
                            GPost.getInstance().addVoteAdvice(sug, vote.voteId);
                            new GMessageDialog(this, "建议添加成功", "成功");
                        }
                    });
                    operator.add(advice);
                }
                break;
            case 1:
                // 投票阶段
                var agree = new GButton("同意");
                var disagree = new GButton("不同意");
                agree.addActionListener(e-> {
                    var msg = GPost.getInstance().vote(mainStatus.getUserId(), vote.voteId, mainStatus.getTargetId(), 1);
                    if (msg.code >= 0) {
                        new GMessageDialog(this, "投票成功！", "成功");
                    } else {
                        new GMessageDialog(this, msg.props, "失败");
                    }
                    updateVote(mainStatus.getTargetId());
                });
                disagree.addActionListener(e-> {
                    var msg = GPost.getInstance().vote(mainStatus.getUserId(), vote.voteId, mainStatus.getTargetId(), -1);
                    if (msg.code >= 0) {
                        new GMessageDialog(this, "投票成功！", "成功");
                    } else {
                        new GMessageDialog(this, msg.props, "失败");
                    }
                    updateVote(mainStatus.getTargetId());
                });
                operator.add(agree);
                operator.add(disagree);
                break;
            case 2:
                var result = new GLabel("投票结束：" + vote.agree + "人同意，" + vote.disagree + "人不同意");
                operator.add(result);
                break;
        }
        pane.add(operator, BorderLayout.SOUTH);
        return pane;
    }

    private GPanel fileCard;

    public File chooseFile() {
        // 这个控件还没有重写，MARK一下
        JFileChooser chooser = new JFileChooser();
        // 限制文件是为了安全考虑。
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "选择一个文件", "jpg", "png", "zip", "exe", "rar", "7z", "psd", "cpp", "mp4", "avi", "flv", "fbx");
        chooser.setFileFilter(filter);

        if(chooser.showOpenDialog(new TextField()) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private void createFileCard() {
        var open = new GButton("打开下载文件夹");
        open.addActionListener(e-> {
            try {
                Desktop.getDesktop().open(new File(GClass.getFileReceivePath()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        var upload = new GButton("上传文件");
        var uploadPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        uploadPane.setBackground(new Color(0xECF0F1));
        upload.addActionListener(e-> {
            // 上传文件
            var file = chooseFile();
            if (file != null) {
                var fileJson = new FileJson();
                fileJson.path = file.getParent() + "\\" + file.getName();
                fileJson.owner = mainStatus.getUserId();
                fileJson.name = file.getName();
                fileJson.length = file.length();
                var msg = GFileClient.getInstance().upload(fileJson);
                if (msg.code >= 0) {
                    new GMessageDialog(this, "文件上传成功", "成功");
                    fileJson.fileId = msg.code;
                    fileJson.classId = mainStatus.getTargetId();
                    GPost.getInstance().addFile(fileJson);
                } else {
                    new GMessageDialog(this, "文件上传失败", "失败");
                }
                updateFile(mainStatus.getTargetId());
            }

        });

        uploadPane.add(open);
        uploadPane.add(upload);

        var pane = new GPanel(new BorderLayout());
        fileCard = new GPanel();
        fileCard.setBackground(new Color(0xF4F6F7));
        fileCard.setLayout(new BoxLayout(fileCard, BoxLayout.Y_AXIS));
        var scrollPane = new GScrollPane(fileCard);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.setBackground(new Color(0xF4F6F7));
        pane.add(scrollPane, BorderLayout.CENTER);
        pane.add(uploadPane, BorderLayout.NORTH);
        cardPane.add(pane, FILE_CARD);
    }

    private void updateFile(String classId) {
        var list = GPost.getInstance().getFileList(new ClassJson(classId));
        if (list == null) return;
        fileCard.removeAll();
        for (var i : list) {
            var pane = createFile(i);
            fileCard.add(pane);
        }
        fileCard.revalidate();
        fileCard.repaint();
    }

    private GPanel createFile(FileJson file) {
        var pane = new GPanel(new BorderLayout());
        pane.setBorder(new EmptyBorder(10, 0, 0, 0));
        pane.setPreferredSize(new Dimension(paneWidth * 8 / 10, 100));
        pane.setMaximumSize(new Dimension(paneWidth * 8 / 10, 100));
        // 添加文件
        //pane.add(createEditorPane(new MarkDownParser().parse(notice.text)), BorderLayout.CENTER);
        var filePane = new GPanel(new FlowLayout());
        var icon = new ImageIcon("images/file.png");
        ImageUtil.scaleImage(icon, 40);
        var image = new GImage(icon);
        filePane.add(image);
        var labelPane = new GPanel();
        labelPane.setPreferredSize(new Dimension(paneWidth * 6 / 10, 30));
        labelPane.setMaximumSize(new Dimension(paneWidth * 6 / 10, 30));
        var label = new GLabel(file.name);
        labelPane.add(label);
        var button = new GButton("下载");
        button.addActionListener(e-> {
            if (!e.getActionCommand().equals("下载")) return;
            GFileClient.getInstance().addDownloadListener((p, l)-> {
                button.setText(p / (double)l + "%");
            }, true);
            var msg = GFileClient.getInstance().download(file.fileId, GClass.getFileReceivePath(), false);
            if (msg.code >= 0) {
                button.setText("下载");
                new GMessageDialog(this, "下载成功", "成功");
            }
        });
        filePane.add(labelPane);
        filePane.add(button);
        pane.add(filePane, BorderLayout.CENTER);
        var infoPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        var sender = GPost.getInstance().getUserInfo(file.owner);
        infoPane.add(new GLabel("上传者：" + sender.username));
        infoPane.setBackground(new Color(0xF0F3F4));
        infoPane.setBorder(new EmptyBorder(0, 0, 0, 30));
        pane.add(infoPane, BorderLayout.SOUTH);
        return pane;
    }

    private GPanel groups;
    private void createGroupPane() {
        // 群组区域面板
        GPanel westPane = new GPanel();
        westPane.setBackground(new Color(0xF7F9F9));
        westPane.setPreferredSize(new Dimension(groupWidth, paneHeight));
        add(westPane, BorderLayout.WEST);

        // 群组标题栏
        GPanel groupTitleBar = new GPanel(new BorderLayout());
        groupTitleBar.setBackground(new Color(0xFDFEFE));
        groupTitleBar.setPreferredSize(new Dimension(groupWidth, chatTitleBarHeight));
        groupTitleBar.setBorder(BorderFactory.createCompoundBorder(groupTitleBar.getBorder(),
                BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0xD0D3D4))));
        GLabel groupTitle = new GLabel("消息列表");
        groupTitle.setFont(GConfig.createFont(18));
        groupTitle.setBorder(BorderFactory.createCompoundBorder(groupTitle.getBorder(),
                BorderFactory.createEmptyBorder(0, 25, 0, 0)));     // Margin
        groupTitleBar.add(groupTitle, BorderLayout.CENTER);

        westPane.add(groupTitleBar, BorderLayout.NORTH);

        // 群组列表
        groups = new GPanel();
        groups.setOpaque(false);
        groups.setBackground(new Color(0xFDFEFE));
        groups.setLayout(new BoxLayout(groups, BoxLayout.Y_AXIS));

        GScrollPane groupPane = new GScrollPane(groups);
        groupPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        groupPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        groupPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        groupPane.getVerticalScrollBar().setUnitIncrement(16);
        groupPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xD0D3D4)));
        groupPane.setPreferredSize(new Dimension(groupWidth, groupHeight));
        westPane.add(groupPane, BorderLayout.CENTER);
    }

    private void initView() {
        GShadowPane contentPane = new GShadowPane(5, 12);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        createTitleBar();
        createChatPane();
        createGroupPane();
    }

    private void showTools(Component invoker, int x, int y) {
        GPopupMenu popupMenu = new GPopupMenu();
        popupMenu.setBackground(Color.BLUE);
        GMenuItem optionItem = new GMenuItem("设置");
        GMenuItem addClass = new GMenuItem("加入班级");
        GMenuItem createClass = new GMenuItem("创建班级");
        popupMenu.add(optionItem);
        popupMenu.add(addClass);
        popupMenu.add(createClass);
        optionItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "设置功能未开启"));
        addClass.addActionListener(e->new AddClassDialog(this, "加入班级"));
        createClass.addActionListener(e->new CreateClassDialog(this, "创建班级"));
        popupMenu.show(invoker, x, y);
    }

    private int startX, startY;
    private void addDragMoveFunction(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int wx = getLocation().x;
                int wy = getLocation().y;
                setLocation(wx + e.getX() - startX, wy + e.getY() - startY);
            }
        });
    }

    private void quit() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var command = e.getActionCommand().trim();
        switch (command) {
            case "聊天":
                switchCardPane(CHAT_CARD);
                break;
            case "文件":
                switchCardPane(FILE_CARD);
                updateFile(mainStatus.getTargetId());
                break;
            case "投票":
                switchCardPane(VOTE_CARD);
                updateVote(mainStatus.getTargetId());
                break;
            case "公告":
                switchCardPane(NOTICE_CARD);
                updateNotice(mainStatus.getTargetId());
                break;
            case "成员":
                if (!mainStatus.isClass()) {
                    new GMessageDialog(this, "似乎出了不可能出现的错误...", "哇哦");
                } else {
                    if (classInfoFrame != null && classInfoFrame.isDisplayable()) {
                        classInfoFrame.toFront();
                    } else {
                        var tmp = new ClassJson();
                        tmp.classid = mainStatus.getTargetId();
                        var classJson = GPost.getInstance().getClassInfo(tmp);
                        classInfoFrame = new ClassInfoFrame(classJson, this, mainStatus.getUserId());
                    }
                }
                break;
        }
    }

    private ClassInfoFrame classInfoFrame;

    /**
     * 得到应该被加入到聊天栏中的聊天条
     * @param user 用户信息
     * @return 创建的聊天条
     */
    private ChatBar addAndGetChatBar(UserJson user) {
        // 判断是否已经被添加
        for (var i : chatBars) {
            if (i.getId().equals(user.userid)) {
                return i;
            }
        }
        user = GPost.getInstance().getUserInfo(user);
        var chatBar = new ChatBar(user.userid, user.username, getAvatar(user.avatar), 0, 40, groupWidth, 70);
        UserJson finalUser = user;
        chatBar.addActionListener(e-> {
            setChatPane(finalUser);
            chatBar.showCount(false);
        });
        addChatBar(chatBar);
        return chatBar;
    }

    private ChatBar addAndGetChatBar(ClassJson json) {
        // 判断是否已经被添加
        for (var i : chatBars) {
            if (i.getId().equals(json.classid)) {
                return i;
            }
        }
        json = GPost.getInstance().getClassInfo(json);
        var chatBar = new ChatBar(json.classid, json.gradeIndex + "级" + json.classIndex + "班", getAvatar(json.avatar), 999, 40, groupWidth, 70);
        ClassJson finalJson = json;
        chatBar.addActionListener(e-> {
            setChatPane(finalJson);
            chatBar.showCount(false);
        });
        addChatBar(chatBar);
        repaintChatBar();
        return chatBar;
    }
    /**
     * 暴露给班级信息的函数，用于修改当前聊天对象
     * @param user 用户信息
     */
    void chatTo(UserJson user) {
        if (user.userid.equals(mainStatus.getUserId())) {
            // 啥玩意啊，你还想整个跟自己的聊天啊
            Debug.log("Edge最帅");
        } else {
            toFront();
            addAndGetChatBar(user).showCount(false);
            setChatPane(user);
            switchCardPane(CHAT_CARD);
        }
    }

    /**
     * 得到目标头像
     * @param avatarId 头像id，也可以说是文件id
     * @return 创建的图标
     */
    private ImageIcon getAvatar(int avatarId) {
        var path = GClass.getImageReceivePath();
        if (!new File(path + "\\" + avatarId).exists()) {
            var msg = GFileClient.getInstance().download(avatarId, path, true);
            return new ImageIcon(msg.props);
        }
        return new ImageIcon(path + "\\" + avatarId);
    }

    private GImage avatarImage;

    /**
     * 设置左上角的头像
     */
    private void setMyAvatar() {
        var avatar = getAvatar(getAvatarId(mainStatus.getUserId(), false));
        avatarImage.setIcon(avatar);
        avatarImage.setPreferredSize(new Dimension(avatarSize, avatarSize));
        avatarImage.setRound();
    }

    private void addChatBar(ChatBar chatBar) {
        chatBars.add(chatBar);
        groups.add(chatBar.getPane());
    }

    private void repaintChatBar() {
        chatBars.sort((a, b) -> {
            if (a.getType() == b.getType()) {
                if (a.isShowCount() && b.isShowCount()) return 0;
                else if (a.isShowCount()) return -1;
                else return 1;
            } else if (a.getType() > b.getType()) {
                return -1;
            } else {
                return 1;
            }
        });
        groups.removeAll();
        for (var i : chatBars) {
            groups.add(i.getPane());
        }
        groups.revalidate();
        groups.repaint();
    }

    /**********************************************************
     *  在获取头像时，每次都是重新进行下载
     *  如果之后还记得，就增加一个校验md5
     *  的功能。
     **********************************************************/
    private List<ChatBar> chatBars = new ArrayList<>();
    private void getMyClass() {
        var list = GPost.getInstance().getMyClass();
        if (list == null) {
            chatTitle.setText("您尚未加入班级哦");
            chatTool.setVisible(false);
            return;
        }
        for (final ClassJson classJson : list) {
            String title = classJson.gradeIndex + "级" + classJson.classIndex + "班";
            var msg = GFileClient.getInstance().download(classJson.avatar, GClass.getImageReceivePath(), true);
            ImageIcon avatar = new ImageIcon(msg.props);
            final var chatBar = new ChatBar(classJson.classid, title, avatar, 999, 40, groupWidth, 70);
            chatBar.addActionListener(e -> {
                setChatPane(classJson);
                chatBar.showCount(false);
            });
            addChatBar(chatBar);
        }
    }


    /**
     * 在窗体加载时的新消息列表
     */
    private void getNewChatBar() {
        var list = GPost.getInstance().getChatUser();
        if (list == null) {
            return;
        }
        for (var userJson : list) {
            String title = userJson.username;
            var msg = GFileClient.getInstance().download(userJson.avatar, GClass.getImageReceivePath(), true);
            ImageIcon avatar = new ImageIcon(msg.props);
            final var chatBar = new ChatBar(userJson.userid, title, avatar, 0, 40, groupWidth, 70);
            chatBar.addActionListener(e -> {
                setChatPane(userJson);
                chatBar.showCount(false);
            });
            addChatBar(chatBar);
        }
    }

    /**
     * 创建聊天气泡
     * @param message 聊天内容
     * @return 创建的聊天气泡
     */
    private GChatBubble createChatBubble(MessageJson message) {
        var bubble = new GChatBubble(!message.sender.equals(mainStatus.getUserId()));
        bubble.setBackground(new Color(bubble.isLeft() ? 0xECF0F1 : 0x5DADE2));
        bubble.setForeground(new Color(bubble.isLeft() ? 0x424949 : 0xFFFFFF));
        switch (message.type) {
            case 3:
                bubble.insertText("我发布了一条新公告，请前往查看。\n");
                break;
            case 4:
                bubble.insertText("我发布了一个新决策，请前往审议。\n");
                break;
            case 5:
                bubble.insertText("决策终稿完成，请前往投票。\n");
                break;
            case 6:
                bubble.insertText("决策投票已完成：\n");
        }
        // 解析消息
        var text = message.text;
        // 移除多余空行
        text = text.replaceAll("(?m)^\\s+$", "");
        var m = Pattern.compile("(?<!\\\\)<([^<>]*)(?<!\\\\)>").matcher(text);
        while (m.find()) {
            var path = GClass.getFileCachePath();
            var file = new File(GClass.getFileCachePath(m.group(1)));
            if (!file.exists()) GFileClient.getInstance().download(Integer.parseInt(m.group(1)), path, true);
            var result = new StringBuffer();
            m.appendReplacement(result, "");
            bubble.insertText(result.toString());
            var img = new ImageIcon(GClass.getFileCachePath(m.group(1)));
            ImageUtil.scaleImage(img, paneWidth / 2);
            // 图片自动换行显示
            bubble.insertText("\n");
            bubble.insertIcon(img);
        }
        var result = new StringBuffer();
        m.appendTail(result);
        bubble.insertText(result.toString());

        return bubble;
    }

    /**
     * 保存聊天记录
     * @param id 用户或班级id
     * @param list 聊天记录列表
     */
    private void saveRecord(String id, List<MessageJson> list) {
        try {
            GFileClient.createDirs(GClass.getRecordPath(mainStatus.getUserId()));
            var path = GClass.getRecordPath(mainStatus.getUserId(), id);
            var file = new File(path);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Debug.error("聊天记录文件创建失败");
                }
            }
            var fout = new FileOutputStream(file, true);
            var writer = new PrintWriter(fout);
            for (var i : list) {
                writer.println(i.toString());
            }
            writer.close();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MessageJson createClassChatMessageJson() {
        var message = new MessageJson();
        message.receiver = mainStatus.getTargetId();
        message.type = 1;
        return message;
    }

    private void addChatBubble(List<MessageJson> list, boolean append) {
        int maxChatCount = 10;
        if (list.size() > maxChatCount && !append) {
            list = list.subList(list.size() - maxChatCount - 1, list.size());
        }
        for (var i : list) {
            addChatBubble(createChatBubble(i), getAvatarId(i.sender, false), GPost.getInstance().getUserInfo(i.sender).username);
        }
    }

    private List<String> getRecord(String id) {
        try {
            var path = GClass.getRecordPath(mainStatus.getUserId(), id);
            var file = new File(path);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            var fin = new FileInputStream(file);
            var reader = new BufferedReader(new InputStreamReader(fin));
            var line = "";
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null)   {
                lines.add(line);
            }
            reader.close();
            fin.close();
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // 重新加载消息记录
    private void reloadRecord(MessageJson message) {
        // 先获取最新的消息记录
        var list = GPost.getInstance().getMessage(message);
        var target = message.type == 0 ? message.sender : message.receiver;
        if (list != null) saveRecord(target, list);
        removeChatBubble();
        var lines = getRecord(target);
        // 最多显示50条消息
        List<MessageJson> msgList = new ArrayList<>();
        for (var i : lines) {
            var msg = MessageJson.parse(i);
            if (msg.type >= 2) {
                var json = GPost.getInstance().getClassInfo(new ClassJson(message.receiver));
                showImportant(msg, json.gradeIndex + "级" + json.classIndex + "班");
            }
            msgList.add(msg);
        }
        // 将消息列表加入气泡
        addChatBubble(msgList, false);
    }

    // 获取群组消息并添加到面板
    private List<MessageJson> getClassMessage(MessageJson message) {
        return GPost.getInstance().getMessage(message);
    }

    // 设置聊天面板内容
    private void setChatPane(UserJson userJson) {
        if (mainStatus.getTargetId().equals(userJson.userid)) return;
        if (!cardPane.isVisible()) cardPane.setVisible(true);
        chatTitle.setText(userJson.username);
        chatTool.setVisible(false);
        var msg = new MessageJson();
        mainStatus.setTargetId(userJson.userid);
        mainStatus.setClass(false);
        msg.sender = userJson.userid;
        msg.receiver = mainStatus.getUserId();
        msg.type = 0;
        switchCardPane(CHAT_CARD);
        reloadRecord(msg);
    }

    // 设置聊天面板内容
    private void setChatPane(ClassJson classJson) {
        if (mainStatus.getTargetId().equals(classJson.classid)) return;
        if (!cardPane.isVisible()) cardPane.setVisible(true);
        chatTitle.setText(classJson.gradeIndex + "级" + classJson.classIndex + "班");
        chatTool.setVisible(true);
        var msg = new MessageJson();
        mainStatus.setTargetId(classJson.classid);
        mainStatus.setClass(true);
        msg.receiver = classJson.classid;
        msg.type = 1;
        var json = new ClassJson();
        json.classid = mainStatus.getTargetId();
        var members = GPost.getInstance().getMembers(json);
        for (var i : members) {
            if (i.userId.equals(mainStatus.getUserId())) {
                if (i.type <= 1) {
                    mainStatus.setAdmin(true);
                } else {
                    mainStatus.setAdmin(false);
                }
                break;
            }
        }
        switchCardPane(CHAT_CARD);
        reloadRecord(msg);
    }

    // 发送信息
    private void sendMessage(String msg) {
        new Thread(()-> {
            try {
                var result = new StringBuffer();
                var m = Pattern.compile("(?<!\\\\)<([^<>]*)(?<!\\\\)>").matcher(msg);
                while (m.find()) {
                    var img = new FileJson();
                    img.owner = mainStatus.getUserId();
                    img.path = m.group(1);
                    m.appendReplacement(result, "<" + FileJson.parse(GFileClient.getInstance().upload(img).props).fileId + ">");
                }
                m.appendTail(result);
                var message = new MessageJson();
                message.type = mainStatus.isClass() ? 1 : 0;
                message.receiver = mainStatus.getTargetId();
                message.sender = mainStatus.getUserId();
                message.time = Calendar.getInstance().getTimeInMillis();
                message.text = result.toString();
                GChatClient.getInstance().sendMessage(message);
            } catch (Exception e) {
                new GMessageDialog(this, "消息发送失败（可能因为插入空图片等原因）", "Error");
            }
        }).start();
    }

    @Override
    public void onReceiveMessage(MessageJson message) {
        // 收到服务器发送的消息
        if (message.type == 0) {
            // 获取真正的接收者，如果发送者是自己那么接收者就是消息的接收者，否则是消息发送者
            var receiver = mainStatus.getUserId().equals(message.sender) ? message.receiver : message.sender;
            var user = new UserJson();
            user.userid = receiver;
            var chatBar = addAndGetChatBar(GPost.getInstance().getUserInfo(user));
            var list = new ArrayList<MessageJson>();
            list.add(message);
            // 1.保存聊天记录
            saveRecord(receiver, list);
            // 判断当前聊天窗口是否是同一个
            if (mainStatus.getTargetId().equals(receiver)) {
                // 当前正是和这条消息中的人聊天
                // 把消息添加到当前聊天窗口
                addChatBubble(list, true);
                chatPane.revalidate();
                Rectangle rect = new Rectangle(0,chatPane.getPreferredSize().height,10,10);
                chatPane.scrollRectToVisible(rect);
            } else {
                // 添加新消息提示
                chatBar.showCount(true);
            }
        } else {
            // 群组消息
            var receiver = message.receiver;
            var json = new ClassJson();
            json.classid = receiver;
            var chatBar = addAndGetChatBar(json); // 将班级添加到列表中
            if (mainStatus.getTargetId().equals(receiver)) {
                // 获取群消息，然后添加到面板
                var list = getClassMessage(createClassChatMessageJson());
                saveRecord(receiver, list);
                addChatBubble(list, true);
                chatPane.revalidate();
                Rectangle rect = new Rectangle(0,chatPane.getPreferredSize().height,10,10);
                chatPane.scrollRectToVisible(rect);
            } else {
                // 添加新消息提示
                chatBar.showCount(true);
            }
            if (message.sender.equals(mainStatus.getUserId())) return;
            Debug.log(message.type);
            showImportant(message, chatBar.getTitle());
        }
    }

    private void showImportant(MessageJson message, String title) {
        if (message.type == 3) {
            // 有新公告
            new GMessageDialog(this, title + "有一条新公告", "重要！");
            updateNotice(mainStatus.getTargetId());
        } else if (message.type == 4) {
            // 有新投票
            new GMessageDialog(this, title + "有新文稿等待审议", "重要！");
            updateVote(mainStatus.getTargetId());
        } else if (message.type == 5) {
            // 有新投票
            new GMessageDialog(this, title + "中文稿已经开始投票", "重要！");
            updateVote(mainStatus.getTargetId());
        }/* else if (message.type == 6) {
            new GMessageDialog(this, title + "中文稿投票已经结束", "重要！");
            updateVote(mainStatus.getTargetId());
        }*/
    }

    private int getAvatarId(String userid, boolean isClass) {
        return GPost.getInstance().getAvatarId(userid, isClass);
    }

    /**
     * 获取旧聊天记录文件夹中的用户
     * @return .
     */
    private List<String> getOldChatUser() {
        List<String> files = new ArrayList<>();
        File file = new File(GClass.getRecordPath(mainStatus.getUserId()));

        File[] tempList = file.listFiles();
        if (tempList == null) return null;
        for (File aTempList : tempList) {
            if (aTempList.isFile()) {
                files.add(aTempList.getName());
            }
        }
        return files;
    }

    /**
     * 聊天记录加载到面板
     */
    private void addOldChat() {
        var list = getOldChatUser();
        if (list == null) return;
        for(var u : list) {
            var user = new UserJson();
            user.userid = u;
            user = GPost.getInstance().getUserInfo(user);
            if (user != null) addAndGetChatBar(user).showCount(false);
        }
    }

    public void onSendClassMessage(String classId, String msg) {
        var message = new MessageJson();
        message.type = 1;
        message.receiver = classId;
        message.sender = mainStatus.getUserId();
        message.time = Calendar.getInstance().getTimeInMillis();
        message.text = msg;
        GChatClient.getInstance().sendMessage(message);
    }
}
