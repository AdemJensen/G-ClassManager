package top.grapedge.ui.component;

import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-07 23:50
 **/
public class GScrollPane extends JScrollPane {
    public GScrollPane(JComponent component) {
        super(component);
        initView();
    }

    public GScrollPane() { super(); initView();}

    private void initView() {
        setOpaque(false);
        getHorizontalScrollBar().setUI(new GScrollBarUI());
        getVerticalScrollBar().setUI(new GScrollBarUI());
    }

    @Override
    protected void paintComponent(Graphics g) {
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        super.paintComponent(g);
    }
}
