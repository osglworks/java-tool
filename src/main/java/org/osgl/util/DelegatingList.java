package org.osgl.util;

/**
 * Implement {@link C.List} with a backing {@link java.util.List} instance
 */
class DelegatingList<T> extends ListBase<T> implements C.List<T> {
    DelegatingList(Iterable<T> iterable) {
    }
}
