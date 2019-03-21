package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.client.base.GFileClient;
import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.server.json.FileJson;
import top.grapedge.gclass.server.json.UserJson;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.component.*;
import top.grapedge.ui.misc.GLimitFilter;
import top.grapedge.ui.misc.GNumberFilter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-11 22:53
 **/
public class RegisterFrame extends GFrame {
    // 窗体高度
    private int width = 540;
    // 窗体宽度
    private int height = 540;
    // 阴影大小
    private int shadowSize = 8;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        // 基础属性设置
        setTitle("Login");
        setUndecorated(true);
        setSize(width, height);
        setLocationRelativeTo(null);
        setBackground(ColorUtil.TRANSPARENT);   // 透明窗体
        initView();
        setVisible(true);
    }

    private int startX, startY;
    private GInput userid, username;
    private GPasswordInput password;
    private void initView() {
        GShadowPane container = new GShadowPane(shadowSize, 10);
        container.setLayout(new BorderLayout());
        setContentPane(container);

        GLowPoly poly = new GLowPoly(width, height / 3, 50, 50);
        poly.setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(poly, BorderLayout.NORTH);

        var close = new GButton();
        ImageIcon closeIcon = new ImageIcon("images/close.png");
        closeIcon.setImage(closeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        close.setIcon(closeIcon);
        close.setRadius(7);
        close.setNormalColor(ColorUtil.TRANSPARENT);
        close.addActionListener(e-> {
            this.dispose();
            loginFrame.setVisible(true);
        });

        var minus = new GButton();
        ImageIcon minusIcon = new ImageIcon("images/minus.png");
        minusIcon.setImage(minusIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        minus.setIcon(minusIcon);
        minus.setRadius(7);
        minus.setNormalColor(ColorUtil.TRANSPARENT);
        minus.addActionListener(e-> {
            setExtendedState(JFrame.ICONIFIED);
        });

        poly.add(minus);
        poly.add(close);

        GPanel inputs = new GPanel(new FlowLayout(FlowLayout.CENTER));
        inputs.setBackground(ColorUtil.BACKGROUND);
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)));
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createEmptyBorder(40, 0, 0, 0)));
        inputs.setPreferredSize(new Dimension((int)(width * 0.7), height / 3));

        GLabel label = new GLabel("用户信息");
        label.setFont(GConfig.createFont(18));
        label.setPreferredSize(new Dimension(300, 30));
        inputs.add(label);
        userid = new GInput(20);
        userid.setHintText("学号");
        userid.setFilter(new GNumberFilter(12));
        userid.setPreferredSize(new Dimension(300, 45));
        inputs.add(userid);

        username = new GInput(20);
        username.setHintText("用户名");
        username.setFilter(new GLimitFilter(6));
        username.setPreferredSize(new Dimension(300, 45));
        inputs.add(username);

        password = new GPasswordInput(20);
        password.setHintText("密码");
        password.setFilter(new GLimitFilter(15));
        password.setPreferredSize(new Dimension(300, 45));
        inputs.add(password);

        register = new GButton("注册");
        register.addActionListener(e->registerEvent());
        register.setPreferredSize(new Dimension(300, 45));
        inputs.add(register);

        add(inputs, BorderLayout.WEST);

        GPanel avatarPane = new GPanel();

        avatarPane.setLayout(new BoxLayout(avatarPane, BoxLayout.Y_AXIS));
        avatarPane.setBorder(BorderFactory.createCompoundBorder(avatarPane.getBorder(), BorderFactory.createEmptyBorder(50, 0, 0, 0)));
        GImage avatar = new GImage(new ImageIcon("images/avatar.jpg"));
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setRound();
        avatarPane.add(avatar);
        avatarPane.setBackground(ColorUtil.BACKGROUND);

        GPanel panel = new GPanel();
        panel.setBackground(ColorUtil.BACKGROUND);
        GImage upload = new GImage(new ImageIcon("images/plus.png"));
        upload.setPreferredSize(new Dimension(50, 50));
        panel.add(upload);
        panel.setBorder(new EmptyBorder(30, 0, 0, 0));
        upload.addActionListener(e-> {
            chooseImage(avatar);
        });
        avatarPane.add(panel);
        add(avatarPane, BorderLayout.CENTER);


        ////////////////////////////////////////////
        setVisible(true);
        addDragMoveFunction();
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

    private String avatarPath = "";
    public void chooseImage(GImage avatar) {
        // 这个控件还没有重写，MARK一下
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "选择JPG或PNG图片", "jpg", "png");
        chooser.setFileFilter(filter);

        if(chooser.showOpenDialog(new TextField()) == JFileChooser.APPROVE_OPTION) {
            var file = chooser.getSelectedFile();
            avatarPath = file.getParent()+ "\\" + file.getName();
            ImageIcon icon = new ImageIcon(avatarPath);
            avatar.setIcon(icon);
            avatar.setPreferredSize(new Dimension(80, 80));
            avatar.setRound();
        }
    }

    private boolean isRegister = false;
    private GButton register;
    public void registerEvent() {
        if (userid.isHint() || userid.getText().length() != 12) {
            new GMessageDialog(this, "学号为12位数字", "信息错误").setVisible(true);
        } else if (username.isHint() || username.getText().length() < 2 || username.getText().length() > 6) {
            new GMessageDialog(this, "请输入正确的用户名", "信息错误");
        } else if (password.isHint() || password.getPassword().length < 6) {
            new GMessageDialog(this, "密码不少于6位数字", "信息错误");
        } else {
            if (!isRegister) {
                register.setText("注册中...");
                isRegister = true;
                new Thread(()-> {
                    // 上传头像
                    var avatarFile = new FileJson();
                    avatarFile.path = avatarPath;
                    avatarFile.owner = userid.getText();
                    var upload = GFileClient.getInstance().upload(avatarFile);
                    var avatarId = -1;
                    if (upload != null) {
                        avatarId = FileJson.parse(upload.props).fileId;
                    }
                    var user = new UserJson();
                    user.userid = userid.getText();
                    user.password = Parser.md5(String.valueOf(password.getPassword()));
                    user.username = username.getText();
                    user.avatar = avatarId;
                    var result = GPost.getInstance().register(user);
                    if (result.code == 0) {
                        new GMessageDialog(this, result.props, "成功");
                        dispose();
                        loginFrame.setVisible(true);
                    } else {
                        new GMessageDialog(this, result.props, "失败");
                    }
                    register.setText("注册");
                    isRegister = false;
                }).start();
            }
        }
    }

}
