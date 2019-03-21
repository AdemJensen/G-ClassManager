package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.base.GClass;
import top.grapedge.gclass.client.base.GFileClient;
import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.client.ui.ChatBar;
import top.grapedge.gclass.server.json.ClassJson;
import top.grapedge.gclass.server.json.MemberJson;
import top.grapedge.gclass.server.json.UserJson;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.component.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Comparator;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-18 19:02
 **/
public class ClassInfoFrame extends GFrame {
    // 窗体高度
    private int width = 450;
    // 窗体宽度
    private int height = 560;
    // 阴影大小
    private int shadowSize = 8;
    private MainFrame mainFrame;
    private String userId;

    private MemberJson user;
    private ClassJson classJson;

    public ClassInfoFrame(ClassJson classJson, MainFrame frame, String userId) {
        this.classJson = classJson;
        this.userId = userId;
        this.mainFrame = frame;
        initView(classJson);
        addMembers(classJson);
        setUndecorated(true);
        setBackground(ColorUtil.TRANSPARENT);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addMembers(ClassJson classJson) {
        var list = GPost.getInstance().getMembers(classJson);
        // 按照群主、管理员、普通用户排序
        list.sort(Comparator.comparingInt(a -> a.type));
        for (var i : list) {
            if (i.userId.equals(userId)) {
                user = i;
                break;
            }
        }
        for (var i : list) {
            addMember(i);
        }
        listPane.revalidate();
        listPane.repaint();
    }

    private int startX, startY;

    private void initView(ClassJson classJson) {
        GShadowPane container = new GShadowPane(shadowSize, 10);
        container.setLayout(new BorderLayout());
        setContentPane(container);

        GLowPoly poly = new GLowPoly(width, 150, 50, 50);
        poly.setLayout(new BorderLayout());
        add(poly, BorderLayout.NORTH);

        GPanel toolPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        var close = new GButton();
        ImageIcon closeIcon = new ImageIcon("images/close.png");
        closeIcon.setImage(closeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        close.setIcon(closeIcon);
        close.setRadius(7);
        close.setNormalColor(ColorUtil.TRANSPARENT);
        close.addActionListener(e-> dispose());
        toolPane.setOpaque(false);
        toolPane.setPreferredSize(new Dimension(width, 50));
        toolPane.add(close);

        poly.add(toolPane, BorderLayout.NORTH);

        GPanel classPane = new GPanel(new BorderLayout());
        classPane.setPreferredSize(new Dimension(width, 100));

        GPanel titlePane = new GPanel();
        titlePane.setOpaque(false);
        GLabel title = new GLabel();
        title.setText(classJson.gradeIndex + "级" + classJson.classIndex + "班");
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GConfig.createFont(30));

        titlePane.add(title);
        classPane.add(titlePane, BorderLayout.NORTH);

        GPanel introPane = new GPanel();
        introPane.setOpaque(false);
        introPane.setPreferredSize(new Dimension(width, 70));
        GLabel intro = new GLabel();
        intro.setText("班级号：" + classJson.classid + "，" + classJson.intro);
        intro.setForeground(Color.WHITE);
        intro.setFont(GConfig.createFont(11));
        introPane.add(intro);

        classPane.setOpaque(false);

        classPane.add(introPane, BorderLayout.CENTER);
        poly.add(classPane, BorderLayout.CENTER);

        GPanel content = new GPanel(new BorderLayout());

        add(content, BorderLayout.CENTER);
        GPanel memberPane = new GPanel(new FlowLayout(FlowLayout.LEFT));
        memberPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        memberPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY), memberPane.getBorder()));
        GLabel member = new GLabel("成员");
        member.setFont(GConfig.createFont(20));
        memberPane.add(member);

        content.add(memberPane, BorderLayout.NORTH);

        listPane = new GPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        var memberScrollPane = new GScrollPane(listPane);
        memberScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        memberScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(memberScrollPane);

        ////////////////////////////////////////////
        addDragMoveFunction();
    }

    private ImageIcon getAvatar(int avatarId) {
        var path = GClass.getImageReceivePath();
        if (!new File(path).exists()) {
            var msg = GFileClient.getInstance().download(avatarId, path, true);
            return new ImageIcon(msg.props);
        }
        return new ImageIcon(path + "\\" + avatarId);
    }

    private GPanel listPane;
    private void addMember(MemberJson member) {
        var tmp = new UserJson();
        tmp.userid = member.userId;
        var user = GPost.getInstance().getUserInfo(tmp);
        var title = user.username;
        if (member.type == 0) title += "（群主）";
        else if (member.type == 1) title += "（管理员）";
        var chatBar = new ChatBar(user.userid, title, getAvatar(user.avatar), 0, 40, width - 20, 70);
        chatBar.showCount(false);
        var finalUser = this.user;
        chatBar.getArea().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && finalUser != null && finalUser.type == 0) {
                    // 显示右键菜单
                    showRightMenu(member, chatBar.getPane(), e.getX(), e.getY());
                } else if (e.getClickCount() == 2) {
                    // 向主界面通信，对该用户聊天
                    mainFrame.chatTo(user);
                }
            }
        });
        listPane.add(chatBar.getPane());
    }

    private void showRightMenu(MemberJson member, Component invoker, int x, int y) {
        if (member.userId.equals(userId)) {
            return;
        }
        var menu = new JPopupMenu();
        var setAdmin = new JMenuItem(member.type == 2 ? "设置为管理员" : "取消管理员");
        menu.add(setAdmin);
        menu.show(invoker, x, y);
        setAdmin.addActionListener(e->{
            if (e.getActionCommand().equals("设置为管理员")) {
                member.type = 1;
                var msg = GPost.getInstance().setAdmin(member);
                if (msg.code == 0) {
                    new GMessageDialog(this, "设置成功", "椰丝");
                    removeMembers();
                    addMembers(classJson);
                } else {
                    new GMessageDialog(this, "这人可能与管理员无缘", "痛击我的队友");
                }
            } else if (e.getActionCommand().equals("取消管理员")) {
                member.type = 2;
                var msg = GPost.getInstance().setAdmin(member);
                if (msg.code == 0) {
                    new GMessageDialog(this, "取消成功", "椰丝");
                    removeMembers();
                    addMembers(classJson);
                } else {
                    new GMessageDialog(this, "这人可能开挂了", "痛击我的队友");
                }
            }
        });

    }

    private void removeMembers() {
        listPane.removeAll();
    }

    private void addDragMoveFunction() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int wx = getLocation().x;
                int wy = getLocation().y;
                setLocation(wx + e.getX() - startX, wy + e.getY() - startY);
            }
        });
    }

}
