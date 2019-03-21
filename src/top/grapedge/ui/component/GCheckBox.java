package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-07 12:12
 **/
public class GCheckBox extends JCheckBox {
    public GCheckBox() {
    }

    public GCheckBox(String text) {
        super(text);
        setFont(GConfig.font);
        setBackground(Color.WHITE);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPaintedFlat(true);
        ImageIcon unselected = new ImageIcon("images/unselected.png");
        unselected.setImage(unselected.getImage().getScaledInstance(13, 13, Image.SCALE_DEFAULT ));
        ImageIcon selected = new ImageIcon("images/selected.png");
        selected.setImage(selected.getImage().getScaledInstance(13, 13, Image.SCALE_DEFAULT));
        setIcon(unselected);
        setSelectedIcon(selected);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d);
        super.paintComponent(g2d);
    }
}
