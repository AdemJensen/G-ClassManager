package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-12 08:53
 **/
public class GMessageDialog extends JDialog {
    public GMessageDialog(Frame parent, String msg, String title) {
        super(parent, title, true);
        initView(msg);
    }

    public GMessageDialog(Dialog parent, String msg, String title) {
        super(parent, title, true);
        initView(msg);
    }

    private void initView(String msg) {
        setResizable(false);
        //setMinimumSize(new Dimension(200, 100));
        GPanel panel = new GPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorUtil.BACKGROUND);
        GLabel label = new GLabel(msg);
        label.setFont(GConfig.createFont(15));

        panel.add(label);

        GPanel button = new GPanel();
        button.setBackground(ColorUtil.BACKGROUND);
        button.setBorder(new EmptyBorder(20, 0, 10, 0));
        GButton confirm = new GButton("确定");
        confirm.setRadius(7);
        confirm.setPreferredSize(new Dimension(80, 30));
        confirm.addActionListener(e->dispose());
        button.add(confirm);

        panel.add(button);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
