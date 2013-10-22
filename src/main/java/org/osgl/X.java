package org.osgl;

public enum X {
    ;
    static interface BaseFoo<T, S extends BaseFoo<T, S>> {
        S foo();
    }

    static interface Foo<T> extends BaseFoo<T, Foo<T>> {
        void foo1();
    }

    static interface BaseBar<T, S extends BaseBar<T, S>> {
        S bar();
    }

    static interface Bar<T> extends Foo<T>, BaseBar<T, Bar<T>> {
        // so foo() here should return a Bar<T> type
        // and bar() here should also return a Bar<T> type ?
        void bar1();
    }

    static abstract class AbstractFooBase<T, S extends AbstractFooBase<T, S>> implements BaseFoo<T, S> {
        abstract void internalFoo();
        @Override
        public S foo() {
            internalFoo();
            return (S)this;
        }
    }

    static class FooImpl<T> extends AbstractFooBase<T, FooImpl<T>> implements Foo<T> {
        @Override
        void internalFoo() {
            System.out.println("inside FooImpl::internalFoo()");
        }

        @Override
        public void foo1() {
            System.out.println("inside FooImpl::foo1()");
        }
    }

    public static void main(String[] args) {
        FooImpl<String> foo = new FooImpl<String>();
        foo.foo().foo1();
    }
}
