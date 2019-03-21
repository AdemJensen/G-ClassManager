package top.grapedge.ui.base.animation.base;

import top.grapedge.gclass.base.Debug;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public enum Animator {
    INSTANCE(60);

    private Timer timer;

    private final List<Animatable> animations = new ArrayList<>();

    Animator(int fps) {
        if (fps <= 0) fps = 60;
        timer = new Timer(1000 / fps, e-> {
            try {
                animations.removeIf(Animatable::update);
                if (animations.isEmpty()) {
                    timer.stop();
                }
            } catch (Exception e1) {
                Debug.log("动画引擎初始化失败，正在重试。");
                timer.restart();
            }
        });
    }

    public void add(Animatable anim) {
        animations.add(anim);
        timer.restart();
    }

    public void remove(Animatable anim) {
        animations.remove(anim);
        if (animations.isEmpty()) {
            timer.stop();
        }
    }
}
