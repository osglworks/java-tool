package org.osgl.util.algo;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 5/11/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ArrayAlgorithm extends Algorithm {
    static enum Util {
        ;
        public static void checkIndex(Object[] array, int index) {
            if (index < 0 || index >= array.length) {
                throw new IndexOutOfBoundsException();
            }
        }

        public static void checkIndex(Object[] array, int from, int to) {
            if (array.length == 0) return;
            if (from < 0 || from >= array.length || to < 0 || to > array.length) {
                throw new IndexOutOfBoundsException();
            }
        }
    }
}
