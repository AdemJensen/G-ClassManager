package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.base.Debug;
import top.grapedge.gclass.base.Parser;
import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.server.json.UserJson;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.base.util.ImageUtil;
import top.grapedge.ui.component.*;
import top.grapedge.ui.misc.GLimitFilter;
import top.grapedge.ui.misc.GNumberFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @program: G-ClassManager
 * @description: 用户登录界面
 * @author: Grapes
 * @create: 2019-03-05 18:33
 **/
public class LoginFrame extends GFrame implements ActionListener {
    // 窗体高度
    private int width = 540;
    // 窗体宽度
    private int height = 416;
    // 阴影大小
    private int shadowSize = 8;

    private GButton login, close, minus;

    public LoginFrame() {
        // 基础属性设置
        setTitle("Login");
        setUndecorated(true);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(ColorUtil.TRANSPARENT);   // 透明窗体
        initView();
        setVisible(true);
    }

    private int startX, startY;
    private void initView() {
        GShadowPane container = new GShadowPane(shadowSize, 10);

        container.setLayout(new BorderLayout());
        setContentPane(container);

        // 低多边形组件
        GRoundPane roundPane = new GRoundPane(10);
        ((FlowLayout)roundPane.getLayout()).setVgap(0);
        GLowPoly poly = new GLowPoly(width, height / 2, 60, 60);

        ((FlowLayout)poly.getLayout()).setVgap(0);
        roundPane.setPreferredSize(new Dimension(width, height / 2));
        roundPane.add(poly);
        GImage logo = new GImage(new ImageIcon("images/logo.png"));
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                Debug.log(startX);
            }

        });

        logo.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int wx = getLocation().x;
                int wy = getLocation().y;
                setLocation(wx + e.getX() - startX, wy + e.getY() - startY);
            }
        });
        logo.setLayout(null);
        poly.add(logo);
        // 关闭按钮
        close = new GButton();
        ImageIcon closeIcon = new ImageIcon("images/close.png");
        closeIcon.setImage(closeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        close.setIcon(closeIcon);
        close.setRadius(7);
        close.setBounds(490, 10, 30, 30);
        close.setNormalColor(ColorUtil.TRANSPARENT);
        close.addActionListener(this);
        logo.add(close);
        // 最小化按钮
        minus = new GButton();
        ImageIcon minusIcon = new ImageIcon("images/minus.png");
        minusIcon.setImage(minusIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        minus.setIcon(minusIcon);
        minus.setRadius(7);
        minus.setBounds(460, 10, 30, 30);
        minus.setNormalColor(ColorUtil.TRANSPARENT);
        minus.addActionListener(this);
        logo.add(minus);

        add(roundPane, BorderLayout.NORTH);

        GPanel inputPane = new GPanel(null);
        inputPane.setBackground(Color.WHITE);


        // 用户名
        accountInput = new GInput(20);
        accountInput.setHintText("学号");
        accountInput.setFilter(new GNumberFilter(12));

        accountInput.setBounds(145, 20, 250, 40);
        // 密码
        passwordInput = new GPasswordInput(20);
        passwordInput.setHintText("密码");
        passwordInput.setFilter(new GLimitFilter(15));
        passwordInput.setBounds(145, 60, 250, 40);

        GCheckBox remPassword = new GCheckBox("记住密码");
        remPassword.setBounds(145, 100, 100, 30);

        GCheckBox autoLogin = new GCheckBox("自动登录");
        autoLogin.addActionListener(e-> {
            if (autoLogin.isSelected()) {
                remPassword.setSelected(true);
            }
        });
        autoLogin.setBounds(315, 100, 100, 30);

        // 登录
        login = new GButton("下一步");
        login.setBounds(145, 130, 250, 40);
        login.setRadius(5);
        login.addActionListener(this);

        // 注册账号和找回密码
        GLabel register = new GLabel("注册账号");
        register.addActionListener(this);
        register.setBounds(400, 20, 100, 40);
        GLabel findPassword = new GLabel("找回密码");
        findPassword.addActionListener(e-> {
            new GMessageDialog(this, "帅气的Grapes咕掉了找回密码的功能", "啦啦啦啦");
        });
        findPassword.setBounds(400, 60, 100, 40);

        GPanel avatarPane = new GPanel(null);

        GImage avatar = new GImage(ImageUtil.getRoundImageIcon(new ImageIcon("images/avatar.jpg").getImage()));

        avatar.setBounds(40, 20, 90, 90);

        avatarPane.add(avatar);

        inputPane.add(accountInput);
        inputPane.add(passwordInput);
        inputPane.add(login);
        inputPane.add(avatar);
        inputPane.add(remPassword);
        inputPane.add(autoLogin);
        inputPane.add(register);
        inputPane.add(findPassword);

        add(inputPane, BorderLayout.CENTER);
        ////////////////////////////////////////////
        setVisible(true);
        //addDragMoveFunction();
    }

    private GInput accountInput;
    private GPasswordInput passwordInput;

    public void loginEvent() {
        if (accountInput.isHint() || passwordInput.isHint()) {
            new GMessageDialog(this, "请输入学号及密码", "错误");
            return;
        }
        // 因为这个地方是用command来判断的，所以无需额外设置变量
        login.setText("登录中...");

        var user = new UserJson();
        user.userid = accountInput.getText();
        user.password = Parser.md5(String.valueOf(passwordInput.getPassword()));
        // 这里放到线程里是为了让用户登录的时候可以操作...
        new Thread(()-> {
            Debug.out("登录：" + user.toString());
            var result = GPost.getInstance().login(user);
            if (result.code == 0) {
                Debug.out("登录成功，加载主界面");
                new MainFrame(user.userid, result.token);
                dispose();
            } else {
                new GMessageDialog(this, result.props, "登录失败");
            }
            login.setText("下一步");
        }).start();
    }

    public void registerEvent() {
        setVisible(false);
        new RegisterFrame(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == close) {
            System.exit(0);
        } else if (e.getSource() == minus) {
            setExtendedState(JFrame.ICONIFIED);
        } else if (e.getActionCommand().equals("下一步")) {
            loginEvent();
        } else if (e.getActionCommand().equals("注册账号")) {
            registerEvent();
        } else if (e.getActionCommand().equals("记住密码")) {
            System.out.println("记住密码");
        }
    }

}
