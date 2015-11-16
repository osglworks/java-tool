package org.osgl.exception;

/**
 * Used by function object to indicate that it doesn't apply to the parameter
 * specified. This exception extends {@link org.osgl.exception.FastRuntimeException}
 * thus performs much better than normal RuntimeExceptions. Here is one
 * example of using {@code NotAppliedException}:
 *
 * <pre>
 * _.F2&lt;Integer, Integer, Integer&gt; divide1 = new _.F1&lt;Integer, Integer, Integer&gt;() {
 *     {@literal @}Override
 *     public int apply(int n, int d) {
 *         if (d == 0) return NotAppliedException();
 *         return n/d;
 *     }
 * }
 * </pre>
 *
 * In the above example thrown out {@code NotAppliedException} when divider is zero is
 * faster than do the calculation directly and let Java thrown out
 */
public class NotAppliedException extends FastRuntimeException {
}
