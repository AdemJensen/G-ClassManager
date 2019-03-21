package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-08 21:51
 **/
public class GMenuItem extends JMenuItem {
    public GMenuItem() {
        setFont(GConfig.font);
        setPreferredSize(new Dimension(100, 30));
    }

    public GMenuItem(String text) {
        this();
        setText(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        super.paintComponent(g);
    }
}
