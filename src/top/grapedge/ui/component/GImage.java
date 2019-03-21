package top.grapedge.ui.component;

import top.grapedge.ui.base.util.ImageUtil;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @program: G-ClassManager
 * @description: 显示图片控件
 * @author: Grapes
 * @create: 2019-03-05 18:18
 **/
public class GImage extends GLabel {
    private ImageIcon icon;

    private String command = "img";

    public void setCommand(String command) {
        this.command = command;
    }

    public GImage(ImageIcon img) {
        this.icon = img;
        setIcon(img);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                actionListener.actionPerformed(new ActionEvent(GImage.this, ActionEvent.ACTION_PERFORMED, command));
            }
        });
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        super.setIcon(icon);
    }

    public GImage(Image img) {
        this(new ImageIcon(img));
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void setSize(Dimension d) {
        setPreferredSize(d);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        if (preferredSize.width > 0 && preferredSize.height > 0) icon.setImage(icon.getImage().getScaledInstance(preferredSize.width, preferredSize.height, Image.SCALE_DEFAULT ));
    }

    public void setRound() {
        this.icon = ImageUtil.getRoundImageIcon(this.icon.getImage());
        setIcon(this.icon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d);
        super.paintComponent(g2d);
    }
}
