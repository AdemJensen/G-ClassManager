package top.grapedge.ui.component;

import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-08 14:31
 **/
public class GScrollBarUI extends MetalScrollBarUI {

    private Color trackColor = new Color(0xF9F9F9);
    private Color thumbColor = new Color(65, 65, 65, 50);

    private JButton b = new JButton() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }
    };

    GScrollBarUI() {

    }


    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(thumbColor);
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        //g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(trackColor);
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return b;
    }

}
