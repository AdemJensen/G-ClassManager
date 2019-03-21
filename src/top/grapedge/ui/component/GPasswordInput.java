package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-07 18:56
 **/
public class GPasswordInput extends JPasswordField implements FocusListener {
    private String hintText;
    private char echoChar;
    public GPasswordInput(int columns) {
        super(columns);
        echoChar = getEchoChar();
        setFont(GConfig.createFont(14));
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
            setEchoChar(echoChar);
            isHint = false;
            setDocument(filter);
            setText("");
            super.setForeground(foreground);
        }
    }


    @Override
    public void focusLost(FocusEvent e) {
        if(getPassword().length <= 0) {
            setEchoChar('\0');
            isHint = true;
            setDocument(new PlainDocument());
            super.setForeground(hintColor);
            setText(hintText);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        RendererUtil.applyQualityRenderingHints((Graphics2D)g);
        super.paintComponent(g);
    }
}


