package top.grapedge.ui.component;

import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.animation.base.Animator;
import top.grapedge.ui.base.animation.color.ColorAnimatable;
import top.grapedge.ui.base.animation.color.ColorRange;
import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;

/**
 * @program: G-ClassManager
 * @description: 标签控件
 * @author: Grapes
 * @create: 2019-03-05 18:22
 **/
public class GLabel extends JLabel {
    private Duration animationTime = Duration.ofMillis(200);
    private ColorAnimatable transitionAnimatable;

    private Color textColor = Color.WHITE;
    private Color normalColor = new Color(0x515A5A);
    private Color hoverColor = new Color(0x616A6B);
    private Color pressColor = new Color(0x424949);

    private boolean isPressed = false;
    private boolean isRollover = false;

    protected ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public GLabel() {}
    public GLabel(String text) {
        super(text);
        setFont(GConfig.font);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isRollover = true;
                setTextColor();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isRollover = false;
                setTextColor();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                setTextColor();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                setTextColor();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                actionListener.actionPerformed(new ActionEvent(GLabel.this, ActionEvent.ACTION_PERFORMED, GLabel.this.getText()));
            }
        });

    }

    private void setTextColor() {
        Color targetColor;
        if (isPressed) {
            targetColor = pressColor;
        } else if (isRollover) {
            targetColor = hoverColor;
        } else {
            targetColor = normalColor;
        }
        setColorTransitionAnimatable(targetColor);
    }

    protected void setColorTransitionAnimatable(Color targetColor) {
        double progress = stopAnimation();
        transitionAnimatable = new ColorAnimatable(
                new ColorRange(getForeground(), targetColor),
                preferredAnimationTime(progress),
                animator -> setForeground((Color) animator.getValue())
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
        RendererUtil.applyQualityRenderingHints((Graphics2D)g, false);
        super.paintComponent(g);
    }


}
