package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.animation.base.Animator;
import top.grapedge.ui.base.animation.color.ColorAnimatable;
import top.grapedge.ui.base.animation.color.ColorRange;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;

/**
 * @program: UIFrame
 * @description: 重构按钮类
 * @author: Grapes
 * @create: 2019-03-04 20:26
 **/
public class GButton extends JButton {
    private Duration animationTime = Duration.ofMillis(200);
    private ColorAnimatable transitionAnimatable;

    private Color textColor = Color.WHITE;
    private Color normalColor = new Color(0x515A5A);
    private Color hoverColor = new Color(0x616A6B);
    private Color pressColor = new Color(0x424949);
    private double radiusWidth = 0.0, radiusHeight;

    public GButton() {
        super();
        setOpaque(false);
        setFocusPainted(false);
        //setBorderPainted(false);
        setBorder(new EmptyBorder(8, 10, 8, 10));
        setFont(GConfig.font);
        setForeground(textColor);
        setContentAreaFilled(false);
        setBackground(normalColor);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setButtonColor();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setButtonColor();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setButtonColor();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setButtonColor();
            }
        });

    }

    public GButton(String text) {
        this();
        setText(text);
    }

    public void setRadius(double radius) {
        this.radiusWidth = this.radiusHeight = radius;
    }

    public void setRadius(double radiusWidth, double radiusHeight) {
        this.radiusWidth = radiusWidth;
        this.radiusHeight = radiusHeight;
    }

    public void setRadiusWidth(double radiusWidth) {
        this.radiusWidth = radiusWidth;
    }

    public void setRadiusHeight(double radiusHeight) {
        this.radiusHeight = radiusHeight;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        setButtonColor();
    }

    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
        setBackground(normalColor);
    }

    public void setPressColor(Color pressColor) {
        this.pressColor = pressColor;
        setButtonColor();
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        setForeground(this.textColor);
    }

    public void setAnimationTime(Duration animationTime) {
        this.animationTime = animationTime;
    }

    protected void setButtonColor() {
        Color targetColor;
        if (getModel().isPressed()) {
            targetColor = pressColor;
        } else if (getModel().isRollover()) {
            targetColor = hoverColor;
        } else {
            targetColor = normalColor;
        }
        setColorTransitionAnimatable(targetColor);
    }

    protected void setColorTransitionAnimatable(Color targetColor) {
        double progress = stopAnimation();
        transitionAnimatable = new ColorAnimatable(
                new ColorRange(getBackground(), targetColor),
                preferredAnimationTime(progress),
                animator -> setBackground((Color) animator.getValue())
        );
        Animator.INSTANCE.add(transitionAnimatable);
    }

    protected double stopAnimation() {
        if (transitionAnimatable != null) {
            Animator.INSTANCE.remove(transitionAnimatable);
            return transitionAnimatable.getProgress();
        }
        return 0.0;
    }

    protected Duration preferredAnimationTime(double currentProgress) {
        if (currentProgress > 0.0 && currentProgress < 1.0) {
            double remainingProgress = 1.0 - currentProgress;
            double runningTime = animationTime.toMillis() * remainingProgress;
            return Duration.ofMillis((long)runningTime);
        }

        return animationTime;
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        // 抗锯齿
        RendererUtil.applyQualityRenderingHints(g2d, true);
        /*绘制按钮*/
        g2d.setColor(getBackground());
        if (radiusWidth < 0.0) radiusWidth = 0.0;
        if (radiusHeight < 0.0) radiusHeight = 0.0;

        int rw = (int)radiusWidth;
        int rh = (int)radiusHeight;
        if (radiusWidth <= 1.0) {
            rw = (int)(getWidth() * radiusWidth);
        }
        if (radiusHeight <= 1.0) {
            rh = (int)(getHeight() * radiusHeight);
        }

        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), rw, rh);

        super.paintComponent(g);
    }

}
