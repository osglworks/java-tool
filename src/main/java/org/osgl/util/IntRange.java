package org.osgl.util;

public class IntRange extends LazyRange<Integer> {

    public IntRange(int from, int to) {
        this(from, to, 1);
    }

    public IntRange(int from, int to, int stepLen) {
        super(from, to, N.F.intRangeStep(stepLen));
    }

    public N.IntRangeStep step() {
        return (N.IntRangeStep)super.step();
    }

    public int get(int id) {
        if (id < 0) {
            return step().times(-id).apply(to(), ordering);
        } else if (0 == id) {
            return from();
        } else {
            return step().times(id).apply(from(), -ordering);
        }
    }

    public static void main(String[] args) {
        IntRange range = new IntRange(0, 10, 2);
        System.out.println(range.get(2));
        range = new IntRange(100, 30);
        System.out.println(range.get(11));
    }

    public static IntRange of(int from, int to) {
        return new IntRange(from, to);
    }

}
