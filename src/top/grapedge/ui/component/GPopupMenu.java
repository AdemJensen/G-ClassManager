package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-08 21:44
 **/
public class GPopupMenu extends JPopupMenu {
    public GPopupMenu() {
        setFont(GConfig.font);
    }

    @Override
    protected void paintComponent(Graphics g) {
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        super.paintComponent(g);
    }
}
