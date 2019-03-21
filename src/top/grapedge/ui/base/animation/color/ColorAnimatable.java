package top.grapedge.ui.base.animation.color;

import top.grapedge.ui.base.Range;
import top.grapedge.ui.base.animation.base.AbstractAnimatable;
import top.grapedge.ui.base.animation.base.AnimatableListener;

import java.awt.*;
import java.time.Duration;

/**
 * @program: UIFrame
 * @description: 颜色动画类
 * @author: Grapes
 * @create: 2019-03-04 20:24
 **/
public class ColorAnimatable extends AbstractAnimatable<Color> {
    public ColorAnimatable(Range<Color> range, Duration duration, AnimatableListener listener) {
        super(range, listener);
        setDuration(duration);
    }
}
