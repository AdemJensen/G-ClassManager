package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.server.json.ClassJson;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.component.*;
import top.grapedge.ui.misc.GNumberFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-12 19:16
 **/
public class AddClassDialog extends JDialog {
    // 窗体高度
    private int width = 540;
    // 窗体宽度
    private int height = 400;
    // 阴影大小
    private int shadowSize = 8;
    private MainFrame mainFrame;

    AddClassDialog(MainFrame parent, String title) {
        super(parent, title);
        this.mainFrame = parent;
        setModal(true);
        initView();
        setUndecorated(true);
        setBackground(ColorUtil.TRANSPARENT);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private int startX, startY;

    private GInput classId;
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
        });

        poly.add(close);

        GPanel inputs = new GPanel(new FlowLayout(FlowLayout.CENTER));
        inputs.setBackground(ColorUtil.BACKGROUND);
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)));
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createEmptyBorder(40, 0, 0, 0)));
        inputs.setPreferredSize(new Dimension(width, height / 3));

        GLabel label = new GLabel("加入班级");
        label.setFont(GConfig.createFont(18));
        label.setPreferredSize(new Dimension(300, 30));
        inputs.add(label);
        classId = new GInput(20);
        classId.setHintText("班级号");
        classId.setFilter(new GNumberFilter(12));
        classId.setPreferredSize(new Dimension(300, 45));
        inputs.add(classId);

        GButton add = new GButton("添加");
        add.addActionListener(e->{if (!classId.isHint()) {
            addEvent(classId.getText());
        }});
        add.setPreferredSize(new Dimension(300, 45));
        inputs.add(add);

        add(inputs, BorderLayout.WEST);

        ////////////////////////////////////////////
        addDragMoveFunction();
    }

    private void addEvent(String classId) {
        var json = new ClassJson();
        json.classid = classId;
        var result = GPost.getInstance().addClass(json);
        if (result.code == 0) {
            new GMessageDialog(this, "加入成功", "圣骑士的斩杀");
            mainFrame.onSendClassMessage(json.classid, "大家好，我是新加入的成员。");
            dispose();
        } else {
            new GMessageDialog(this, result.props, "法力值不足");
        }
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
