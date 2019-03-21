package top.grapedge.ui.component;

import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-12 23:13
 **/
public class GGroupButton extends GButton {
    private boolean showCount = true;
    private int countSize = 14;
    private int margin = 12;

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
        getParent().repaint();
    }

    public boolean isShowCount() {
        return showCount;
    }

    public GGroupButton(String text) {
        super(text);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d, true);
        super.paintComponent(g2d);
        if (!showCount)
            return;
        ImageIcon icon = new ImageIcon("images/tip.png");
        icon.setImage(icon.getImage().getScaledInstance(countSize, countSize, Image.SCALE_DEFAULT));
        g.drawImage(icon.getImage(), getWidth() - countSize - margin, getHeight() / 2 - countSize / 2, null);
    }
}
