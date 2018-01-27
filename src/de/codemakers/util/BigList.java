package de.codemakers.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BigList<T> implements List<T> {

    public static final BigInteger MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    public static final BigInteger MINUS_MAX_VALUE = BigInteger.valueOf(-Integer.MAX_VALUE);

    private BigInteger index;
    private final List<T> data;
    private BigList<T> child = null;
    private Supplier<List<T>> supplier = ArrayList::new;
    private Function<Collection<? extends T>, List<T>> function = ArrayList::new;

    public BigList(List<T> data) {
        this(BigInteger.ZERO, data);
    }

    public BigList(List<T> data, Supplier<List<T>> supplier) {
        this(BigInteger.ZERO, data);
        if (supplier != null) {
            this.supplier = supplier;
        }
    }

    public BigList(Supplier<List<T>> supplier) {
        this(BigInteger.ZERO, supplier.get());
    }

    private BigList(BigInteger index, List<T> data) {
        this.index = index;
        if (data != null) {
            this.data = data;
        } else {
            this.data = supplier.get();
        }
    }

    public final T get(BigInteger i) {
        BigList list = this;
        while (i.compareTo(MAX_VALUE) >= 0) {
            i = i.subtract(MAX_VALUE);
            list = list.child;
            if (list == null) {
                return null;
            }
        }
        return child.data.get(i.intValue());
    }

    private final BigList createChild() {
        if (child != null) {
            return child;
        }
        child = new BigList(supplier);
        return child;
    }

    public final BigList forEachBigList(Consumer<BigList> consumer) {
        BigList list = this;
        while (list != null) {
            consumer.accept(list);
            list = list.child;
        }
        return this;
    }

    public final BigList getLastBigList() {
        BigList list = this;
        while (list.child != null) {
            list = list.child;
        }
        return list;
    }

    @Override
    public int size() {
        final BigInteger size = BigInteger.ZERO;
        forEachBigList((bigList) -> size.add(BigInteger.valueOf(bigList.data.size())));
        return size.intValue();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty() && child == null;
    }

    @Override
    public boolean contains(Object object) {
        BigList list = this;
        while (list != null) {
            if (list.data.contains(object)) {
                return true;
            }
            list = list.child;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() { //TODO ...
        return null;
    }

    @Override
    public Object[] toArray() { //TODO Make n-dimensional array?
        return data.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) { //TODO Make n-dimensional array?
        return data.toArray(a);
    }

    @Override
    public boolean add(T t) {
        BigList list = this;
        while (list != null) {
            if (list.data.size() >= Integer.MAX_VALUE) {
                if (list.child == null) {
                    list.createChild();
                }
                list = list.child;
            } else {
                list.data.add(t);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object object) {
        BigList list = this;
        while (list != null) {
            if (list.data.remove(object)) {
                return true;
            }
            list = list.child;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (collection == null) {
            return true;
        }
        return collection.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        if (collection == null) {
            return true;
        }
        boolean allAdded = true;
        for (T t : collection) {
            if (!add(t)) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) { //TODO Make Method with BigInteger
        if (collection == null) {
            return true;
        }
        boolean allAdded = true;
        final int size = data.size();
        final int size_left = Integer.MAX_VALUE - size;
        final List<T> list = function.apply(collection);
        if (collection.size() >= size_left) {
            if (!data.addAll(index, list.subList(0, size_left))) {
                allAdded = false;
            }
            if (!createChild().addAll(list.subList(size_left, list.size()))) {
                allAdded = false;
            }
        } else {
            return data.addAll(index, collection);
        }
        return allAdded;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (collection == null) {
            return false;
        }
        boolean allRemoved = true;
        for (Object object : collection) {
            if (!remove(object)) {
                allRemoved = false;
            }
        }
        return allRemoved;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        if (collection == null) {
            return false;
        }
        boolean allRetained = true;
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (!contains(object)) {
                iterator.remove();
            }
        }
        return allRetained;
    }

    @Override
    public void clear() {
        BigList list = this;
        while (list != null) {
            list.data.clear();
            list = list.child;
        }
        child = null;
    }

    @Override
    public T get(int index) { //TODO Make Method with BigInteger
        return data.get(index);
    }

    @Override
    public T set(int index, T element) { //TODO Make Method with BigInteger
        return data.set(index, element);
    }

    @Override
    public void add(int index, T element) { //TODO Make Method with BigInteger
        if (data.size() >= Integer.MAX_VALUE) {
            createChild().add(element);
        } else {
            data.add(index, element);
        }
    }

    @Override
    public T remove(int index) { //TODO Make Method with BigInteger
        return data.remove(index);
    }

    @Override
    public int indexOf(Object object) { //TODO Make Method with BigInteger
        return data.indexOf(object);
    }

    @Override
    public int lastIndexOf(Object object) { //TODO Make Method with BigInteger
        return data.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() { //TODO
        return null;
    }

    @Override
    public ListIterator<T> listIterator(int index) { //TODO
        return null;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) { //TODO Make Method with BigInteger
        return data.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return "BigList{" + "index=" + index + ", data=" + data + ", child=" + child + '}';
    }

}
