package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @program: G-ClassManager
 * @description: 输入框
 * @author: Grapes
 * @create: 2019-03-06 09:43
 **/
public class GInput extends JTextField implements FocusListener {
    private String hintText;
    public GInput(int columns) {
        super(columns);
        setFont(GConfig.createFont(15));
        setBorder(new GInputBorder(new Color(192, 192, 192), 1 , true));
        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        addFocusListener(this);
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        focusLost(null);
    }

    private Color foreground = Color.BLACK;
    private Color hintColor = Color.GRAY;
    @Override
    public void setForeground(Color fg) {
        this.foreground = fg;
    }

    public void setHintColor(Color hintColor) {
        this.hintColor = hintColor;
    }

    private boolean isHint = true;

    public boolean isHint() {
        return isHint;
    }

    private PlainDocument filter = new PlainDocument();

    public void setFilter(PlainDocument filter) {
        this.filter = filter;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isHint) {
            isHint = false;
            setDocument(filter);
            setText("");
            super.setForeground(foreground);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        super.paintComponent(g);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(getText().length() == 0) {
            isHint = true;
            setDocument(new PlainDocument());
            super.setForeground(hintColor);
            setText(hintText);
        }

    }


}

class GInputBorder extends LineBorder {

    public GInputBorder(Color color, int thickness, boolean roundedCorners) {
        super(color, thickness, roundedCorners);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        Graphics2D g2 = (Graphics2D)g;
        int i;
        g2.setRenderingHints(rh);
        g2.setColor(lineColor);
        for(i = 0; i < thickness; i++)  {
            if(!roundedCorners)
                g2.drawRect(x+i, y+i, width-i-i-1, height-i-i-1);
            else
                g2.drawRoundRect(x+i, y+i, width-i-i-1, height-i-i-1, 5, 5);
        }
        g2.setColor(oldColor);
    }
}