package top.grapedge.ui.component;

import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.base.util.RendererUtil;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-06 20:55
 **/
public class GRoundPane extends GPanel {

    private  int arc;

    public GRoundPane(int arc) {
        this.arc = arc;
        setOpaque(false);
        setBackground(ColorUtil.TRANSPARENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d);
        g2d.clip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
