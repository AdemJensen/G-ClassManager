package top.grapedge.ui.base.animation.color;

import top.grapedge.ui.base.Range;
import top.grapedge.ui.base.util.ColorUtil;

import java.awt.*;

/**
 * @program: UIFrame
 * @description: 颜色过渡
 * @author: Grapes
 * @create: 2019-03-04 20:19
 **/
public class ColorRange extends Range<Color> {
    public ColorRange(Color from, Color to) {
        super(from, to);
    }

    @Override
    public Color valueAt(double progress) {
        return ColorUtil.blend(getTo(), getFrom(), progress);
    }

}
