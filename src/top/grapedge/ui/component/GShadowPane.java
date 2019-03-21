package top.grapedge.ui.component;

import top.grapedge.ui.base.util.ImageUtil;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * @program: G-ClassManager
 * @description: 阴影面板
 * @author: Grapes
 * @create: 2019-03-06 08:38
 **/
public class GShadowPane extends JPanel {
    private BufferedImage shadow;

    private int arc;

    public GShadowPane(int px, int arc) {
        this.arc = arc;
        setOpaque(false);
        setBorder(new EmptyBorder(px, px, px, px));
        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        layout.setHgap(0);
        setLayout(layout);
    }

    @Override
    public void invalidate() {
        shadow = null;
        super.invalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d);
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - (insets.left + insets.right);
        int height = getHeight() - (insets.top + insets.bottom);

        if (shadow == null && width > 0 && height > 0) {
            int shadowWidth = Math.min(Math.min(insets.left, insets.right), Math.min(insets.top, insets.bottom));
            shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D _g2d = shadow.createGraphics();
            _g2d.setColor(getBackground());
            _g2d.fillRect(0, 0, width, height);
            _g2d.dispose();
            shadow = ImageUtil.generateShadow(shadow, shadowWidth, Color.BLACK, 0.8f);
        }
        g2d.drawImage(shadow, 0, 0, this);
        g2d.setPaint(getBackground());
        g2d.clip(new RoundRectangle2D.Double(x, y, width, height, arc, arc));
        g2d.fillRect(x, y, width, height);
    }


}
