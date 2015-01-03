package org.osgl.concurrent;

public abstract class ContextLocalBase<T> extends ContextLocal.InitialValueProvider<T> implements ContextLocal<T>  {
    private InitialValueProvider<T> iv;

    protected ContextLocalBase() {}

    protected ContextLocalBase(InitialValueProvider<T> ivp) {
        iv = ivp;
    }

    @Override
    public T initialValue() {
        if (null == iv) return null;
        return iv.initialValue();
    }
}
