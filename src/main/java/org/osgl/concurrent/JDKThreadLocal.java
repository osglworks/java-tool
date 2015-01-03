package org.osgl.concurrent;

/**
 * Implement {@link org.osgl.concurrent.ContextLocal} using
 * JDK ThreadLocal
 */
public class JDKThreadLocal<T> extends ContextLocalBase<T> implements ContextLocal<T> {

    private volatile ThreadLocal<T> tl;

    public JDKThreadLocal() {}

    public JDKThreadLocal(InitialValueProvider<T> ivp) {
        super(ivp);
    }

    private ThreadLocal<T> tl() {
        if (null == tl) {
            synchronized (this) {
                if (null == tl) {
                    final JDKThreadLocal<T> me = this;
                    tl = new ThreadLocal<T>() {
                        @Override
                        protected T initialValue() {
                            return me.initialValue();
                        }
                    };
                }
            }
        }
        return tl;
    }

    @Override
    public T get() {
        return tl().get();
    }

    @Override
    public void set(T value) {
        tl().set(value);
    }

    @Override
    public void remove() {
        tl().remove();
    }

}
