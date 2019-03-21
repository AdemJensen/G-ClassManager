package top.grapedge.ui.base.animation.base;

import top.grapedge.ui.base.Range;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @program: UIFrame
 * @description:
 * @author: Grapes
 * @create: 2019-03-04 20:03
 **/
public class AbstractAnimatable<T> implements Animatable {

    private Range<T> range;
    private LocalDateTime startTime;    // 动画开始时间
    private Duration duration = Duration.ofSeconds(2);      // 动画时长，默认为2s
    private T value;        // 当前动画值

    private AnimatableListener listener;

    public AbstractAnimatable(Range<T> range, AnimatableListener listener) {
        this.range = range;
        this.value = range.getFrom();
        this.listener = listener;
    }

    @Override
    public Range<T> getRange() {
        return range;
    }

    @Override
    public T getValue() {
        return value;
    }

    public double getProgress() {
        startTime = startTime == null ? LocalDateTime.now() : startTime;
        return Duration.between(startTime, LocalDateTime.now()).toMillis() / (double)getDuration().toMillis();
    }

    @Override
    public boolean update() {
        double progress = getProgress();
        //System.out.println(progress);
        progress = progress > 1.0 ? 1.0 : progress;
        value = getRange().valueAt(progress);
        listener.stateChanged(this);
        return progress >= 1.0;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }
}
