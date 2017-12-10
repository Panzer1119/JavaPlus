package de.codemakers.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * ArrayUtil for Arrays, Lists and Maps
 *
 * @author Paul Hagedorn
 */
public class ArrayUtil {

    /**
     * Changes the length of an array (shorter or longer)
     *
     * @param <T> Type of the objects in the array
     * @param original Array
     * @param newLength New length
     * @return New array
     */
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    /**
     * Changes the length and type of an array (shorter or longer)
     *
     * @param <T> Type of the objects in the new array
     * @param <U> Type of the objects in the old array
     * @param original Array
     * @param newLength New length
     * @param newType Class of the new type
     * @return New array
     */
    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength] : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, newLength);
        return copy;
    }

    /**
     * Searches for an object in an array
     *
     * @param <T> Type of the array
     * @param array Array
     * @param toTest Object to be contained in the array
     * @return <tt>true</tt> if the object is in the array
     */
    public static <T> boolean contains(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        for (T t : array) {
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * Searches for objects in an array
     *
     * @param <T> Type of the array
     * @param array Array
     * @param toTest Objects to be contained in the array
     * @return <tt>true</tt> if all objects are in the array
     */
    public static <T> boolean contains(T[] array, T... toTest) {
        return contains(array, null, toTest);
    }

    /**
     *
     * Searches/Filters for objects in an array
     *
     * @param <T> Type of the array
     * @param array Array
     * @param filter Filter or standard equals-filter
     * @param toTest Objects to be contained in the array
     * @return <tt>true</tt> if all objects are in the array
     */
    public static <T> boolean contains(T[] array, Filter<T> filter, T... toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        if (filter == null) {
            filter = Filter.createFilterEquals();
        }
        if (toTest.length == 0) {
            return true;
        }
        for (T t_1 : toTest) {
            boolean found = false;
            for (T t_2 : array) {
                if (filter.filter(t_1, t_2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public interface Filter<T> {

        /**
         * Filters two objects
         *
         * @param arrayEntry First object
         * @param toTestEntry Second object
         * @return <tt>true</tt> if they pass the test
         */
        public boolean filter(T arrayEntry, T toTestEntry);

        /**
         * Returns an equals-filter
         *
         * @param <T> Type of the objects
         * @return Filter that filters all equal objects
         */
        public static <T> Filter<T> createFilterEquals() {
            return (T arrayEntry, T toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry == toTestEntry || arrayEntry.equals(toTestEntry) || toTestEntry.equals(arrayEntry));
            };
        }

        /**
         * Returns an equalIgnoreCase-filter for Strings
         *
         * @return Filter that filters all equalIgnoreCase Strings
         */
        public static Filter<String> createStringFilterEqualsIgnoreCase() {
            return (String arrayEntry, String toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry.equalsIgnoreCase(toTestEntry) || toTestEntry.equalsIgnoreCase(arrayEntry));
            };
        }

        /**
         * Returns an always-filter
         *
         * @param <T> Type of the objects
         * @return Filter that filters all objects
         */
        public static <T> Filter<T> createFilterAlways() {
            return (T arrayEntry, T toTestEntry) -> {
                return true;
            };
        }

        /**
         * Returns an never-filter
         *
         * @param <T> Type of the objects
         * @return Filter that filters no objects
         */
        public static <T> Filter<T> createFilterNever() {
            return (T arrayEntry, T toTestEntry) -> {
                return false;
            };
        }

    }

    /**
     * Serial sorts an ArrayList
     *
     * @param <T> Type of the objects in the ArrayList
     * @param list ArrayList
     * @param c Comparator
     */
    public static final <T> void sortArrayListAsArray(List<T> list, Comparator<? super T> c) {
        final T[] array = (T[]) list.toArray();
        list.clear();
        Arrays.sort(array, c);
        list.addAll(Arrays.asList(array));
    }

    /**
     * Parallel sorts an ArrayList
     *
     * @param <T> Type of the objects in the ArrayList
     * @param list ArrayList
     * @param c Comparator
     */
    public static final <T> void parallelSortArrayListAsArray(List<T> list, Comparator<? super T> c) {
        final T[] array = (T[]) list.toArray();
        list.clear();
        Arrays.parallelSort(array, c);
        list.addAll(Arrays.asList(array));
    }

}
