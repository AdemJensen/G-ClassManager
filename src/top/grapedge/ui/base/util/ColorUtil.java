package top.grapedge.ui.base.util;

import java.awt.*;

/**
 * @program: UIFrame
 * @description: 颜色混合类
 * @author: Grapes
 * @create: 2019-03-04 20:20
 **/
public class ColorUtil {
    // 透明色
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color BACKGROUND = new Color(0xFDFEFE);
    // 一个十分简单的颜色过渡功能
    public static Color blend(Color from, Color to, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float red = from.getRed() * r + to.getRed() * ir;
        float green = from.getGreen() * r + to.getGreen() * ir;
        float blue = from.getBlue() * r + to.getBlue() * ir;
        float alpha = from.getAlpha() * r + to.getAlpha() * ir;

        red = Math.min(255f, Math.max(0f, red));
        green = Math.min(255f, Math.max(0f, green));
        blue = Math.min(255f, Math.max(0f, blue));
        alpha = Math.min(255f, Math.max(0f, alpha));

        Color color = null;
        try {
            color = new Color((int) red, (int) green, (int) blue, (int) alpha);
        } catch (IllegalArgumentException exp) {
            exp.printStackTrace();
        }
        return color;
    }
}
