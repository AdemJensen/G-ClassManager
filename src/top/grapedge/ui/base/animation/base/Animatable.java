package top.grapedge.ui.base.animation.base;

import top.grapedge.ui.base.Range;

import java.time.Duration;

public interface Animatable<T> {
    Range<T> getRange();
    T getValue();
    boolean update();
    void setDuration(Duration duration);
    Duration getDuration();
}
