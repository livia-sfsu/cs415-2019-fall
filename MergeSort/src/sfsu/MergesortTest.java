package sfsu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

//import static org.assertj.core.api.Assertions.*;

class MergesortTest {

    static final int SHORT_SIZE = 10;
    static final int MEDIUM_SIZE = 100;
    static final int LONG_SIZE = 10_000;

    @BeforeAll
    static void warmUp() {
        test(LONG_SIZE);
    }

    @Test
    void sizeShort() {
        test(SHORT_SIZE);
    }

    @Test
    void sizeMedium() {
        test(MEDIUM_SIZE);
    }

    @Test
    void sizeLong() {
        test(LONG_SIZE);
    }

    static void test(int size) {
        if (size == 0) {
            return;
        }

        ArrayList<Integer> unsorted = randomArray(size);
        Collection<Integer> sorted = Mergesort.sort(unsorted);
        assertSorted(unsorted, sorted);
    }

    static <T extends Comparable<? super T>> void assertSorted(
            final Iterable<Integer> unordered, final Iterable<Integer> sorted) {
        assertThat(sorted).containsAll(unordered);
        assertThat(isSorted(sorted));

    }

    static <T extends Comparable<? super T>> boolean isSorted(final Iterable<T> iterable) {
        Iterator<T> i = iterable.iterator();
        if (!i.hasNext()) {
            return true;
        }

        T current = i.next();
        while (i.hasNext()) {
            T next = i.next();
            if (current.compareTo(next) == 1) {
                return false;
            }
        }
        return true;
    }

    static ArrayList<Integer> randomArray(final int size) {
        ArrayList<Integer> result = new ArrayList<>(size);
        Random randy = new Random();
        for (int i = 0; i < size; i++) {
            result.add(randy.nextInt());
        }
        return result;
    }

}