package org.osgl.util;

import java.util.*;
import static org.osgl.util._.F.*;

/**
 * A Collection utility provides number of methods help manipulating collections including List and Set.
 * 
 * <p>Also the namespace under which the osgl defined List and Set classes are located</p>
 * 
 * <p>A concept of osgl collection is it distinguish between mutable and immutable versions. As a general rule
 * for most osgl extended mutable methods like {@link Col#with(Object) with}, 
 * {@link Col#keep(java.util.Collection)}  keep}, {@link Col#without(Object)}  without} etc, an new collection   
 * instance is returned; while for immutable version, the same method will operate on the current collection   
 * instance and return reference to this instance directly</p> 
 * 
 * @author Gelin Luo
 * @version 0.2
 * @see Col
 * @see List
 */
public final class C {

    /**
     * make constructor private
     */
    private C() {}

    /**
     * Defines common methods of List and Set
     * 
     * @param <ET>
     */
    protected static abstract class Col<ET> implements Collection<ET> {

        /**
         * The original collection
         */
        protected final Collection<ET> c_;

        /**
         * indicate whether the Col is readOnly (immutable) or not (mutable)
         */
        private final boolean ro_;
        
        protected final boolean mutable() {
            return !ro_;
        }
        
        /**
         * Return a collection instance with elements in the specified iterable, make sure
         * the returned collection is mutable or immutable as per <code>mutable</code>
         * parameter specified
         * 
         * @param itr the iterable of elements
         * @param mutable indicate the returned collection should be immutable or mutable
         * @return a collection meet the requirement
         */
        protected abstract Collection<ET> createRawCollection_(Iterable<ET> itr, boolean mutable);

        /**
         * Return an new Col instance contains all elements included in this Col instance. The
         * mutaiblity of the new Col instance is defined by the mutable parameter specified
         * 
         * @param mutable indicate the mutability of the new Col instance returned
         * @return the new Col instance
         */
        protected abstract <C extends Col<ET>> C copy_(boolean mutable); 

        /**
         * Return a Col based <code>this</code> Col instance based on the mutability of this Col:
         * <ul>
         * <li>If this Col is immutable and the specified mutable parameter is false, then 
         * return this Col instance directly</li>
         * <li>If this Col is immutable and the specified mutable parameter is true, then
         * return an new mutable Col instance contains all elements of this Col</li>
         * <li>If this Col is mutable and the specified mutable parameter is false, then
         * return an new immutable Col instance contains all elements of this Col</li>
         * <li>If this Col is mutable and the specified mutable parameter is true, then
         * reeturn an new mutable Col instance contains all elements of this Col</li>
         * </ul>
         * 
         * @param mutable indicate the mutability of the returned Col instance
         * @return the Col instance based on the mutability of this Col instance and the mutable parameter
         */
        protected final <C extends Col<ET>> C copyIfMutable_(boolean mutable) {
            if (ro_ && !mutable) {
                return (C)this;
            } else {
                return copy_(mutable);
            }
        }

        private Collection<ET> createRawCollection(Iterable<ET> itr, boolean mutable) {
            while (itr instanceof Col) {
                itr = ((Col) itr).c_;
            }
            return createRawCollection_(itr, mutable);
        }

        /**
         * Construct a Col instance using specified collection
         * @param itr specify the elements to be populated into the Col
         * @param mutable indicates the mutabilty of the Col instance been created
         */
        protected Col(Iterable<ET> itr, boolean mutable) {
            E.NPE(itr);
            c_ = createRawCollection(itr, mutable);
            ro_ = mutable;
        }
        
        // --- Object interfaces

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj.getClass().equals(getClass())) {
                Col that = (Col)obj;
                return that.ro_ == ro_ && that.c_.equals(c_);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return X.hc(c_, getClass());
        }

        @Override
        public String toString() {
            return c_.toString();
        }

        // --- EOF Object interfaces
    
        // --- Collection interfaces 
        @Override
        public boolean add(ET e) {
            return c_.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends ET> c) {
            return c_.addAll(c);
        }

        @Override
        public void clear() {
            c_.clear();
        }
        
        @Override
        public boolean contains(Object o) {
            return c_.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c_.containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return c_.isEmpty();
        }

        @Override
        public Iterator<ET> iterator() {
            return c_.iterator();
        }

        @Override
        public boolean remove(Object o) {
            return c_.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return c_.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return c_.retainAll(c);
        }

        @Override
        public int size() {
            return c_.size();
        }

        @Override
        public Object[] toArray() {
            return c_.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return c_.toArray(a);
        }
        // --- EOF Collection interfaces 
        
        // --- OSGL extensions to general collection
        
        /**
         * Determine if all elements in this Col satisfy a predicate 
         * 
         * @param predicate a function used to test the element
         * @return <code>true</code> if all elements in this Col satisfy the predicate specified, 
         *         or <code>false</code> otherwise
         * @since 0.2
         * @see #any
         * @see #none
         */
        public boolean all(final _.IFunc1<Boolean, ET> predicate) {
            return C.all(c_, predicate);
        }

        /**
         * Determine if there are any elements satisfy a predicate 
         * 
         * @param predicate a function used to test the element
         * @return <code>true</code> if any elements in this Col satisfy the predicate specified
         *         or <code>false</code> otherwise
         * @since 0.2
         * @see #all
         * @see #none
         */
        public boolean any(final _.IFunc1<Boolean, ET> predicate) {
            for (ET e : c_) {
                if (predicate.apply(e)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Count the number of elements that satisfy the predicate specified
         * 
         * @param predicate a function used to test the element
         * @return the number of elements satisfy the predicate
         * @throws NullPointerException if there are null elements in this Col and the predicate
         *         does not permit null parameter (optional)
         * @since 0.2
         */
        public int count(final _.IFunc1<Boolean, ET> predicate) {
            int n = 0;
            for (ET e: c_) {
                if (predicate.apply(e)) {
                    n++;
                }
            }
            return n;
        }

        /**
         * Alias of {@link #uniq()}
         * 
         * @since 0.2
         */
        public <C extends Col<ET>> C distinct() {
            return uniq();
        }

        /**
         * Alias of {@link #select(_.IFunc1)}
         * 
         * @since 0.2
         */
        public <C extends Col<ET>> C filter(final _.IFunc1<Boolean, ET> predicate) {
            return select(predicate);
        }

        /**
         * Return the first element that satisfy the predicate specified. It is up to the sub class
         * implementation to determine the order of elements been iterated.
         * 
         * @param predicate the function to test the element 
         * @return an option value containing the first element in the linked list that satisfies p, 
         *         or None if none exists.
         * @since 0.2
         */
        public _.Option<ET> find(final _.IFunc1<Boolean, ET> predicate) {
            for (ET e : c_) {
                if (predicate.apply(e)) {
                    return _.Some(e);
                }
            }
            return _.None();
        }

        /**
         * Run reducer upon elements in this Col and return the final result. It is up to the 
         * sub class implementation to determine the order of the elements been iterated.
         * 
         * <p>The reducer takes two parameters of the same type, i.e. the type of the elements
         * contained in this Col</p>
         * 
         * @param reducer a function reduces two element type parameter into one variable of the same type
         * @return an option value contains the calculation result with the same type of the elements 
         *         contained in this Col if it is not empty, or None if it is empty
         * @since 0.2
         */
        public _.Option<ET> fold(_.IFunc2<ET, ET, ET> reducer) {
            if (0 == size()) {
                return _.None();
            }
            Iterator<ET> it = c_.iterator();
            ET v = it.next();
            while (it.hasNext()) {
                v = reducer.apply(it.next(), v);
            }
            return _.Some(v);
        }

        /**
         * Run reducer upon elements in this Col based on a giving init value and return final result.
         * It is up to the sub class implementation to determine the order of the elements been iterated.
         * 
         * <p>Like the reducer of {@link #fold(_.IFunc2)}, the reducer of this method takes
         * two parameters; Unlike the former reducer, the first parameter is one of the elements in
         * the Col, the second parameter is the type of the initial value however</p>
         * 
         * @param initVal the initial value of type T
         * @param reducer a function reduces one element and one T typed variable into another T typed variable
         * @param <T> the type of the initial value
         * @return an option value contains the final result of the type of the initial value
         * @since 0.2
         */
        public <T> _.Option<T> fold(T initVal, _.IFunc2<T, ET, T> reducer) {
            if (0 == size()) {
                return _.Some(initVal);
            }
            T v = initVal;
            for (ET e : c_) {
                v = reducer.apply(e, v);
            }
            return _.Some(v);
        }

        /**
         * Return one arbitrary element in the Col. It is up to the sub class implementation to determine which
         * element inside the Col be returned.
         * 
         * @return an option value contains an arbitrary element from this Col or None if the Col is empty
         */
        public _.Option<ET> fetch() {
            if (0 == size()) {
                return _.None();
            }
            return _.Some(c_.iterator().next());
        }

        /**
         * Alias of {@link #keep(java.util.Collection)}
         * 
         * @since 0.2
         */
        public <C extends Col<ET>> C intersect(final Collection<? extends ET> c) {
            return keep(c);
        }

        /**
         * Return a Col that contains all elements contained in both this Col instance and
         * the collection c specified.
         * 
         * <p>The behavior of this function is different according the the mutability of
         * this Col. See {@link #without(Object)} for details</p>
         * 
         * @param c the collection in which the elements should be kept in the result Col
         * @return A Col instance contains elements in both this and the collection specified
         * @throws NullPointerException if there are <code>null</code> value in this Col and 
         *         the specified collection does not permit null value (optional).
         * @since 0.2
         */
        public <C extends Col<ET>> C keep(final Collection<? extends ET> c) {
            return without(new _.If<ET>(){
                @Override
                public boolean eval(ET et) {
                    return !c.contains(et);
                }
            });
        }

        /**
         * Return a Col that contains all elements contained in this Col instance and satsify the
         * predicate specified.
         * 
         * <p>The behavior of this function is different according the the mutability of
         * this Col. See {@link #without(Object)} for details</p>
         * 
         * @param predicate indicate which elements in this Col should be kept in the result Col
         * @return a Col contains only elements satisfy the predicate specified in this Col
         * @since 0.2
         */
        public <C extends Col<ET>> C keep(final _.IFunc1<Boolean, ET> predicate) {
            return without(X.f.not(predicate));
        }

        /**
         * Alias of {@link #size()}
         * 
         * @since 0.2
         */
        public final int len() {
            return size();
        }

        /**
         * Alias of {@link #size()}
         * 
         * @since 0.2
         */
        public final int length() {
            return size();
        }

        /**
         * Run a mapper (from ET to T) on this Col and return an new Col instance contains the same number of type T 
         * elements mapped from all ET elements of this Col
         * 
         * @param mapper the mapper function takes type ET parameter and return a type T result
         * @param <T> the result element type
         * @param <C> the type of resulting Col
         * @return the Col contains transformed elements from this Col
         * @since 0.2
         */
        public abstract <T, C extends Col<T>> C map(final _.IFunc1<T, ET> mapper);
        
        /**
         * Determine if there are any elements satisfy a predicate. This is the inverse method of 
         * {@link #any(_.IFunc1)}
         * 
         * @param predicate a function used to test the element
         * @return <code>false</code> if any elements in this Col satisfy the predicate specified
         *         or <code>true</code> otherwise
         * @since 0.2
         * @see #all
         * @see #any
         */
        public boolean none(final _.IFunc1<Boolean, ET> predicate) {
            for (ET e : c_) {
                if (predicate.apply(e)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Alias of {@link #fold(_.IFunc2)}
         *
         * @since 0.2 
         */
        public _.Option<ET> reduce(_.IFunc2<ET, ET, ET> reducer) {
            return fold(reducer);
        }

        /**
         * Alias of {@link #fold(Object, _.IFunc2)}
         *
         * @since 0.2
         */
        public <T> _.Option<T> reduce(T initVal, _.IFunc2<T, ET, T> reducer) {
            return fold(initVal, reducer);
        }

        /**
         * Return an new Col instance contains all elements from this Col that satisfy the 
         * predicate specified.
         * 
         * <p>The behavior of this method is very like {@link #keep(_.IFunc1)}. The  
         * only differences between the two is when this method operates on an mutable Col, it will  
         * not change the underline Col instance, instead, it returns an new mutable Col instance</p>
         * 
         * @param predicate the function indicate which elements should be selected into the new Col
         * @return an new Col contains selected elements from this Col
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C select(_.IFunc1<Boolean, ET> predicate); 

        /**
         * Alias of {@link #map(_.IFunc1)}
         * 
         * @since 0.2
         */
        public <T, C extends Col<T>> C transform(final _.IFunc1<T, ET> mapper) {
            return map(mapper);
        }

        /**
         * Return a Col contains all elements of this Col and the element specified. It is up to the subclass
         * to determine where the specified element is inserted.
         * 
         * <p>Note the behavior of this method is different according to the mutability of this Col</p>
         * 
         * <ul>
         * <li>For immutable Col, return an new immutable copy of this Col plus the specified element</li>
         * <li>For mutable Col, the specified element is added to this Col and return this reference directly</li>
         * </ul>
         * 
         * @param e the new element needs to be appended to the resulting Col
         * @return A Col instance with all elements of this Col plus the specified element
         * @throws NullPointerException if the specified element is null and the subclass 
         *         does not support null value (optional) 
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C with(ET e);
        
        public <C extends Col<ET>> C with(_.Option<ET> e) {
            if (e.notDefined()) {
                return (C)this;
            }
            return with(e.get());
        }

        /**
         * Return a Col contains all elements of this Col and the specified collection. It is up to the
         * subclass to determine where the new elements are inserted.
         * 
         * <p>Note the behavior of this method is different according to the mutablity of this Col</p>
         * 
         * <ul>
         * <li>For immutable Col, return an new immutable copy of this Col plus the specified elements</li>
         * <li>For mutable Col, the specified elements is added to this Col and return this reference directly</li>
         * </ul>
         * 
         * 
         * @param c collection in which all elements should be appended to the resulting Col
         * @return A Col instance with all elements of this Col plus all elements in the collection specified
         * @throws NullPointerException if there are null elements in the specified collection and the 
         *         subclass does not permit null element
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C with(Collection<? extends ET> c);

        /**
         * Return a Col instance that contains all elements of the current Col instance except
         * the element specified.
         * 
         * <p>The behavior of this method is different as per the mutability state of this 
         * Col instance: </p>
         * 
         * <ul>
         * <li>For immutable Col, this method will create an new immutable Col instance contains
         * all elements of <code>this</code> Col instance except the element specified if it
         * is contained in this Col; If the element specified is not contained in this Col, then
         * reference to this Col instance is returned directly</li>
         * <li>For mutable Col, this method will remove the specified element from <code>this</code>
         * Col instance and return <code>this</code> Col instance directly</li>
         * </ul>
         * 
         * @param e the element that should be excluded in the result Col instance
         * @return A Col instance contains all elements in this Col except the element specified
         * @throws <code>NullPointerException</code> if the element specified is <code>null</code>
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C without(final ET e);

        /**
         * A null safe version of {@link #without(Object)}, i.e. the element is specified by an 
         * {@link _.Option option}. If the option is defined, then the element is fetched from
         * the option value and {@link #without(Object)} is called; otherwise a reference to
         * this Col is returned directly
         * 
         * @param e the element specified in {@link _.Option}
         * @return A Col instance contains all elements in this Col except the element specified
         * @since 0.2
         */
        public final <C extends Col<ET>> C without(final _.Option<ET> e) {
            if (e.isDefined()) {
                return without(e.get());
            } else {
                return (C)this;
            }
        }

        /**
         * Return a Col instance that contains all elements in <code>this</code>
         * Col except those contained in the specified collection.
         * 
         * <p>The behavior of this method is different according to the mutability
         * of <code>this</code> Col instance. See {@link #without(Object)} for
         * detail.</p>
         * 
         * @param c the collection in which elements should be excluded from the
         *          resulting Col instance
         * @return a Col instance contains all elements in this Col except those
         *         contained in the collection specified
         * @throws NullPointerException if there are null value in this Col instance
         *         and the specified collection c does not permit null value (optional)
         * @since 0.2
         */
        public <C extends Col<ET>> C without(final Collection<? extends ET> c) {
            return without(new _.If<ET>(){
                @Override
                public boolean eval(ET et) {
                    return c.contains(et);
                }
            });
        }

        /**
         * Return a Col instance that contains all elements in this Col except
         * those satisfy the predicate specified
         * 
         * <p>The behavior of this method is different according to the mutability
         * of <code>this</code> Col instance. See {@link #without(Object)} for
         * detail.</p>
         * 
         * @param predicate A function indicate the elements that should be excluded from
         *                  the result Col instance
         * @return a Col instance contains all elements in this Col but not matches 
         *         the predicate specified
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C without(final _.IFunc1<Boolean, ET> predicate);

        /**
         * Return a Col that contains no duplicate elements based on this Col instance.
         * 
         * <p>Note the behavior is different according to the mutability of this Col</p>
         * 
         * <ul>
         * <li>For immutable Col, if there are duplicate elements found then an new Col
         * is created and all elements from this Col is copied to the new Col with duplicate
         * elements filtered out; if there are no duplicate elements then this Col is returned</li>
         * <li>For mutable col, duplicate elements are removed from this Col and this Col
         * is returned</li>
         * </ul>
         * 
         * @return a Col contains all elements in this Col with duplicate elements filtered out
         * @since 0.2
         */
        public abstract <C extends Col<ET>> C uniq();

        /**
         * Alias of {@link #uniq()}
         * 
         * @return a Col contains all elements in this Col with duplicate elements filtered out
         * @since 0.2
         */
        public <C extends Col<ET>> C unique() {
            return uniq();
        }
        // --- EOF OSGL extensions to general collection
    }

    /**
     * Define the factory to create {@link java.util.List java List} instance.
     * 
     * <p>Usually the factory should try to create non random access List as
     * osgl will use the factory to create the read-write List. Non random 
     * access List (e.g. java.util.LinkedList) is fast to manipulate</p>
     */
    public static interface IListFactory {
        /**
         * Create an empty <code>java.util.List</code> contains the generic type E 
         * 
         * @param <ET> the generic type of the list element
         * @return A java List instance contains elements of generic type E
         */
        <ET> java.util.List<ET> create();

        /**
         * Create a <code>java.util.List</code> pre populated with elements
         * of specified collection
         * 
         * @param collection the collection whose elements are to be placed into this list
         * @param <ET> the generic type of the list element
         * @return The List been created
         * @throws <code>NullPointerException</code> if the specified collection is null
         */
        <ET> java.util.List<ET> create(Collection<ET> collection);

        /**
         * Create a <code>java.util.List</code> with initial capacity
         * 
         * @param initialCapacity
         * @param <ET> the generic type of the list element
         * @return the list been created
         */
        <ET> java.util.List<ET> create(int initialCapacity);
    }

    /**
     * The osgl List implements Java's {@link java.util.List} interface and provides additional 
     * utility methods to facilitate functional programming.
     */
    public static class List<ET> extends Col<ET> implements java.util.List<ET> {

        private List(Iterable<ET> itr) {
            super(itr, true);
        }
        
        protected List(Iterable<ET> itr, boolean mutable) {
            super(itr, mutable);
        }

        protected final java.util.List<ET> l_() {
            return (java.util.List)c_;
        }
        
        protected final IListFactory f_() {
            if (mutable()) {
                return listFact;
            } else {
                return randomAccessListFact;
            }
        }
        
        protected final <T> List<T> create_(Collection<T> c, boolean mutable) {
            Collection<T> c0 = c;
            if (c instanceof List) {
                List l = (List)c;
                if (!mutable && !l.mutable()) {
                    return l;
                }
                c0 = l.c_;
            }
            if (c instanceof Col) {
                c0 = ((Col) c).c_;
            }
            if (mutable) {
                return new List<T>(c0);
            } else {
                return new ImmutableList<T>(c0);
            }
        }
        
        // --- java.util.List interfaces

        @Override
        public boolean addAll(int index, Collection<? extends ET> c) {
            return l_().addAll(index, c);
        }

        @Override
        public ET get(int index) {
            return l_().get(index);
        }

        @Override
        public ET set(int index, ET element) {
            return l_().set(index, element);
        }

        @Override
        public void add(int index, ET element) {
            l_().add(index, element);
        }

        @Override
        public ET remove(int index) {
            return l_().remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return l_().indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return l_().lastIndexOf(o);
        }

        @Override
        public ListIterator<ET> listIterator() {
            return l_().listIterator();
        }

        @Override
        public ListIterator<ET> listIterator(int index) {
            return l_().listIterator(index);
        }

        @Override
        public java.util.List<ET> subList(int fromIndex, int toIndex) {
            return l_().subList(fromIndex, toIndex);
        }
        // --- eof java.util.List interfaces
        
        // --- implement Col methods

        @Override
        protected <C extends Col<ET>> C copy_(boolean mutable) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected Collection<ET> createRawCollection_(Iterable<ET> itr, boolean mutable) {
            java.util.List<ET> l;
            if (itr instanceof Collection) {
                l = f_().create((Collection<ET>)itr);
            } else {
                l = f_().create();
                for (ET e: itr) {
                    l.add(e);
                }
            }
            if (!mutable) {
                l = Collections.unmodifiableList(l);
            }
            return l;
        }

        @Override
        public <T, C extends Col<T>> C map(_.IFunc1<T, ET> mapper) {
            java.util.List<T> l = f_().create(c_.size());
            for (ET e : c_) {
                l.add(mapper.apply(e));
            }
            return (C)create_(l, mutable());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends Col<ET>> C select(_.IFunc1<Boolean, ET> predicate) {
            java.util.List<ET> l = f_().create(c_.size());
            for (ET e : c_) {
                if (predicate.apply(e)) {
                    l.add(e);
                }
            }
            return (C)create_(l, mutable());
        }

        @Override
        public <C extends Col<ET>> C with(ET e) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <C extends Col<ET>> C with(_.Option<ET> e) {
            return super.with(e);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public <C extends Col<ET>> C with(Collection<? extends ET> c) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <C extends Col<ET>> C without(ET e) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <C extends Col<ET>> C without(_.IFunc1<Boolean, ET> predicate) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <C extends Col<ET>> C uniq() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        // --- eof implement Col methods
        
        // --- OSGL list extensions
        // --- eof OSGL list extensions
    }


    /**
     * The osgl readonly list implements an unmodifiable {@link List} based on the osgl {@link List} 
     * 
     * <p>It is marked as a {@link java.util.RandomAccess} meaning the implementation support fast random access.</p> 
     */
    public static class ImmutableList<ET> extends List<ET> implements RandomAccess {
        private ImmutableList() {
            super(Collections.EMPTY_LIST, false);
        }
        protected ImmutableList(Iterable<ET> itr) {
            super(itr, false);
        }
    }

    /**
     * "osgl.list.factory", the property key to configure user defined {@link IListFactory list factory}. 
     * 
     * Upon loaded, osgl tried to get a class name string from system properties use this
     * configuration key. If osgl find the String returned is not empty then it will initialize
     * the list factory use the class name configured. If any exception raised during the
     * intialization, then it might cause the JVM failed to boot up 
     */
    public static final String CONF_LIST_FACTORY = "osgl.list.factory";

    /**
     * "osgl.random_access_list.factory", the property key to configure user defined {@link IListFactory
     * random access list factory}. See {@link #CONF_LIST_FACTORY} for how osgl use this configuration
     */
    public static final String CONF_RANDOM_ACCESS_LIST_FACTORY = "osgl.random_access_list.factory";

    private static IListFactory listFact;
    private static IListFactory randomAccessListFact;

    /* 
     * -------------------------------------------------------------------------------------------------
     * initialize the factories (list, random access list)
     * ------------------------------------------------------------------------------------------------- 
     */
    static {
        String s = System.getProperty(CONF_LIST_FACTORY);
        if (S.notEmpty(s)) {
            listFact = (IListFactory) X.newInstance(X.classForName(s));
        } else {
            listFact = new IListFactory() {
                @Override
                public <E> java.util.List<E> create() {
                    return new LinkedList<E>();
                }

                @Override
                public <E> java.util.List<E> create(Collection<E> collection) {
                    return new LinkedList<E>(collection);
                }

                @Override
                public <ET> java.util.List<ET> create(int initialCapacity) {
                    return new LinkedList<ET>();
                }
            };
        }

        s = System.getProperty(CONF_RANDOM_ACCESS_LIST_FACTORY);
        if (S.notEmpty(s)) {
            randomAccessListFact = (IListFactory) X.newInstance(X.classForName(s));
        } else {
            randomAccessListFact = new IListFactory() {
                @Override
                public <E> java.util.List<E> create() {
                    return new ArrayList<E>();
                }

                @Override
                public <E> java.util.List<E> create(Collection<E> collection) {
                    return new ArrayList<E>(collection);
                }

                @Override
                public <E> java.util.List<E> create(int initialCapacity) {
                    return new ArrayList<E>(initialCapacity);
                }
            };
        }
    }

    /**
     * The singleton instance of C, could be used by, for example, in a velocity template
     * which is not easy to do static reference
     */
    public static final C INSTANCE = new C();

    /**
     * An empty immutable list. 
     * 
     * <p>This example illustrates the type-safe way to obtain an empty list:
     * <pre>
     *     C.List&lt;String&gt; s = C.list();
     * </pre>
     * </p>
     * 
     * @see #list() 
     * @since 0.2
     */
    public static final ImmutableList EMPTY_LIST = new ImmutableList();
    
    // --- OSGL collection factories

    /**
     * Create an empty immutable list
     * 
     * <p>Implementation notes, this method will not create an new {@link ImmutableList} 
     * instance every time, instead it returns {@link #EMPTY_LIST} directly</p>
     * 
     * @see #EMPTY_LIST
     * @since 0.2
     */
    public static <T> ImmutableList<T> list() {
        return EMPTY_LIST;
    }

    /**
     * Create an immutable list with an existing immutable list.
     * 
     * <p>Implementation notes, this method returns the reference to the specified 
     * immutable list directly</p>
     * 
     * @param l
     * @return the list specified directly
     * @since 0.2
     */
    public static <T> ImmutableList<T> list(ImmutableList<T> l) {
        return l;
    }

    /**
     * Create an immutable list with elements specified in an {@link Iterable}
     * 
     * @since 0.2
     */
    public static <T> ImmutableList<T> list(Iterable<T> itr) {
        if (itr instanceof ImmutableList) {
            return (ImmutableList<T>)itr;
        }
        return new ImmutableList<T>(itr);
    }

    /**
     * Create an immutable list with specified elements 
     * 
     * @since 0.2
     */
    public static <T> ImmutableList<T> list(T ... es) {
        return new ImmutableList<T>(Arrays.asList(es));
    }
    
    public static ImmutableList<Byte> list(byte[] a) {
        int len = a.length;
        java.util.List<Byte> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Character> list(char[] a) {
        int len = a.length;
        java.util.List<Character> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Short> list(short[] a) {
        int len = a.length;
        java.util.List<Short> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Integer> list(int[] a) {
        int len = a.length;
        java.util.List<Integer> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Long> list(long[] a) {
        int len = a.length;
        java.util.List<Long> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Float> list(float[] a) {
        int len = a.length;
        java.util.List<Float> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    public static ImmutableList<Double> list(double[] a) {
        int len = a.length;
        java.util.List<Double> l = randomAccessListFact.create(len);
        for (int i = 0; i < len; ++i) {
            l.add(a[i]);
        }
        return list(l);
    }

    /**
     * The namespace for factory methods to create mutable collections
     */
    public static class Mutable {

        /**
         * Create an empty mutable list
         * 
         * @since 0.2
         */
        public static <T> List<T> list() {
            java.util.List<T> l = Collections.emptyList();
            return new List<T>(l);
        }

        /**
         * Create a mutable list with elements specified in an {@link Iterable} 
         * 
         * @since 0.2
         */
        public static <T> List<T> list(Iterable<T> itr) {
            return new List<T>(itr);
        }

        /**
         * Create an mutable list with elements specified as varargs
         * 
         * @since 0.2
         */
        public static <T> List<T> list(T ... es) {
            return new List<T>(Arrays.asList(es));
        }
    }
    // --- eof OSGL collection factories
    private static <R, I> R foreach(final Iterable<I> itr, final _.IFunc1<Void, I> func, R def) {
        for (I e: itr) {
            try {
                func.apply(e);
            } catch (_.Break b) {
                return b.get();
            }
        }
        return def;
    }

    // --- OSGL collection extensions
    public static <T> boolean all(Iterable<T> itr, final _.IFunc1<Boolean, T> predicate) {
        return foreach(itr, breakIf(not(predicate)), true);
    }
    
    public static <T> boolean any(Iterable<T> itr, final _.IFunc1<Boolean, T> predicate) {
        return foreach(itr, breakIf(predicate), false);
    }
    
    public static <T1, T2> List<T2> map(java.util.List<? extends T1> list, _.IFunc1<T2, T1> mapper) {
        java.util.List<T2> l0;
        boolean readonly = list instanceof RandomAccess;
        if (readonly) {
            l0 = new ArrayList<T2>(list.size());
        } else {
            l0 = new LinkedList<T2>();
        }
        for (T1 t1 : list) {
            l0.add(mapper.apply(t1));
        }
        return new List<T2>(l0);
    }
    // --- eof OSGL collection extensions
}
