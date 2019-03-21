package top.grapedge.ui.base;

/**
 * @program: UIFrame
 * @description: Range
 * @author: Grapes
 * @create: 2019-03-04 19:45
 **/
public abstract class Range<T> {
    private T from;
    private T to;

    public Range(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "From" + getFrom() + " To " + getTo();
    }

    public abstract T valueAt(double progress);
}
