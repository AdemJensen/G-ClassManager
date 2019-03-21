package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * @program: G-ClassManager
 * @description: 聊天气泡类
 * @author: Grapes
 * @create: 2019-03-16 11:43
 **/
public class GChatBubble extends GPanel {
    private GTextPane textPane;
    private boolean isLeft;

    public boolean isLeft() {
        return isLeft;
    }

    /**
     * 用于计算气泡应该有的实际高度。
     * @param  width 期望的宽度
     * @param document 当前文档内容
     * @return 高度
     */
    private int getContentHeight(int width, StyledDocument document) {
        var testPane = new GTextPane();
        testPane.setSize(width, Short.MAX_VALUE);
        testPane.setStyledDocument(document);
        return testPane.getPreferredSize().height;
    }

    public GChatBubble(boolean isLeft) {
        //setLayout(new BorderLayout());
        this.isLeft = isLeft;
        // 在绘制气泡时左偏移了10个像素，然后期望和边框距离6像素
        if (isLeft) {
            setBorder(new EmptyBorder(6, 16, 6, 6));
        } else {
            setBorder(new EmptyBorder(6, 6, 6, 16));
        }
        textPane = new GTextPane();
        textPane.setFont(GConfig.font);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        add(textPane);
        // 默认使用白色做背景
        setBackground(Color.WHITE);
    }


    // 高级操作通过这个进行
    public GTextPane getTextPane() {
        return textPane;
    }

    public void insertIcon(Icon icon) {
        getTextPane().setCaretPosition(getTextPane().getStyledDocument().getLength());
        getTextPane().insertIcon(icon);
    }

    /**
     * 设置气泡的最佳宽度，此方法消耗双倍运算能力，因此建议只调用一次以获得最小的损失
     * @param width
     */
    public void setPreferredSize(int width) {
        var h = getContentHeight(width, getTextPane().getStyledDocument());
        getTextPane().setPreferredSize(new Dimension(width, h));
    }

    public void insertText(String text) {
        var doc = getTextPane().getStyledDocument();
        StyleContext context = new StyleContext();
        Style style = context.addStyle("font", null);
        StyleConstants.setForeground(style, getForeground());
        getTextPane().setCaretPosition(doc.getLength());
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RendererUtil.applyQualityRenderingHints(g2d);
        g2d.setPaint(getBackground());
        if (isLeft) {
            paintLeftBubble(g2d);
        } else {
            paintRightBubble(g2d);
        }
    }

    private void paintRightBubble(Graphics2D g2d) {
        var width = getWidth();
        var height = getHeight();
        var path = new GeneralPath();
        path.moveTo(width - 10, 18);
        path.lineTo(width, 13);
        path.lineTo(width - 10, 8);
        path.curveTo(width - 10, 8, width - 10, 0, width - 15, 0);
        path.lineTo(5, 0);
        path.curveTo(5, 0, 0, 0, 0, 5);
        path.lineTo(0, height - 5);
        path.curveTo(0, height - 5, 0, height, 5, height);
        path.lineTo(width - 15, height);
        path.curveTo(width - 15, height, width - 10, height, width - 10, height - 5);
        path.lineTo(width - 10, 18);
        path.closePath();
        g2d.fill(path);
    }

    private void paintLeftBubble(Graphics2D g2d) {
        var width = getWidth();
        var height = getHeight();
        var path = new GeneralPath();
        path.moveTo(10, 18);
        path.lineTo(0, 13);
        path.lineTo(10, 8);
        path.curveTo(10, 8, 10, 0, 15, 0);
        path.lineTo(width - 5, 0);
        path.curveTo(width - 5, 0, width, 0, width, 5);
        path.lineTo(width, height - 5);
        path.curveTo(width, height - 5, width, height, width - 5, height);
        path.lineTo(15, height);
        path.curveTo(15, height, 10, height, 10, height - 5);
        path.lineTo(10, 18);
        path.closePath();
        g2d.fill(path);
    }
}
