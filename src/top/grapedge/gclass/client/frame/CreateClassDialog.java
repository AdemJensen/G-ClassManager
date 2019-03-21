package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.server.json.ClassJson;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.component.*;
import top.grapedge.ui.misc.GLimitFilter;
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
public class CreateClassDialog extends JDialog {
    // 窗体高度
    private int width = 540;
    // 窗体宽度
    private int height = 540;
    // 阴影大小
    private int shadowSize = 8;

    private MainFrame mainFrame;
    public CreateClassDialog(MainFrame parent, String title) {
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

    private GInput classIndex, gradeIndex, intro;
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
        close.addActionListener(e-> this.dispose());

        poly.add(close);

        GPanel inputs = new GPanel(new FlowLayout(FlowLayout.CENTER));
        inputs.setBackground(ColorUtil.BACKGROUND);
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)));
        inputs.setBorder(BorderFactory.createCompoundBorder(inputs.getBorder(), BorderFactory.createEmptyBorder(40, 0, 0, 0)));
        inputs.setPreferredSize(new Dimension(width, height / 3));

        GLabel label = new GLabel("创建班级");
        label.setFont(GConfig.createFont(18));
        label.setPreferredSize(new Dimension(300, 30));
        inputs.add(label);



        gradeIndex = new GInput(20);
        gradeIndex.setHintText("年级");
        gradeIndex.setFilter(new GNumberFilter(4));
        gradeIndex.setPreferredSize(new Dimension(300, 45));
        inputs.add(gradeIndex);

        classIndex = new GInput(20);
        classIndex.setHintText("班级");
        classIndex.setFilter(new GNumberFilter(4));
        classIndex.setPreferredSize(new Dimension(300, 45));
        inputs.add(classIndex);

        intro = new GInput(20);
        intro.setHintText("一句话介绍");
        intro.setFilter(new GLimitFilter(12));
        intro.setPreferredSize(new Dimension(300, 45));
        inputs.add(intro);

        GButton add = new GButton("创建");
        add.addActionListener(e-> {
            if (gradeIndex.isHint() || classIndex.isHint() || intro.isHint()) {
                new GMessageDialog(this, "请输入信息", "哼");
                return;
            }
            createEvent(gradeIndex.getText(), classIndex.getText(), intro.getText());
        });
        add.setPreferredSize(new Dimension(300, 45));
        inputs.add(add);

        add(inputs, BorderLayout.WEST);

        ////////////////////////////////////////////
        addDragMoveFunction();
    }

    private void createEvent(String gradeId, String classId, String introText) {
        if (gradeId.length() != 4) {
            new GMessageDialog(this, "年级为四位数字", "没想到吧");
        } else if (classId.length() == 0 || classId.length() > 4) {
            new GMessageDialog(this, "班级编号不对劲鸭", "没想到吧");
        } else if (introText.length() > 12) {
            new GMessageDialog(this, "介绍不能超过十二个字", "没想到吧");
        } else {
            var json = new ClassJson();
            json.gradeIndex = Integer.parseInt(gradeId);
            json.classIndex = Integer.parseInt(classId);
            json.avatar = -1;
            json.intro = introText;
            json.classid = String.format("%04d%04d", json.gradeIndex, json.classIndex);
            var result = GPost.getInstance().createClass(json);
            if (result.code == 0) {
                new GMessageDialog(this, result.props, "想到了");
                mainFrame.onSendClassMessage(json.classid, "我创建了班级：" + json.gradeIndex + "级" + json.classIndex + "班");
                dispose();
            } else {
                new GMessageDialog(this, result.props, "没想到吧");
            }
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
