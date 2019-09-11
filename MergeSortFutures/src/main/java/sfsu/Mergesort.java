package sfsu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import com.google.common.util.concurrent.Futures;


public class Mergesort<T extends Comparable> {

    static final int THREADS = 10_000;
    ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    public Collection<T> sort(Collection<T> collection) {

        Collection<T> result = null;
        try {
            Future<Collection<T>> f = executor.submit(
                    () -> doSort(new ArrayList<>(collection)));
            result = f.get();
        }
        catch (ExecutionException | InterruptedException ie) {
            System.err.println("I failed at threads. What went wrong?");
            ie.printStackTrace();
            System.exit(-1);
        }
        finally {
            return result;
        }

    }

    /**
     * Recursive implementation of mergesort.
     * @param collection
     * @return
     */
    private ArrayList<T> doSort(ArrayList<T> collection)
            throws ExecutionException, InterruptedException {

        // Leaf cases: 1 or 2 elements left.
        if (collection.size() == 1) {
            return collection;
        }
        if (collection.size() == 2) {
            Iterator<T> i = collection.iterator();
            T a = i.next();
            T b = i.next();

            ArrayList<T> result = new ArrayList<>(2);
            result.add(a.compareTo(b) <= 0 ? a : b);
            result.add(b.compareTo(a) == 1 ? b : a);
            return collection;
        }

        // Split
        final ArrayList<T> left = new ArrayList<>(collection.size()/2);
        final ArrayList<T> right = new ArrayList<>(collection.size()/2);

        Iterator<T> i = collection.iterator();
        for (int count = 0; count < collection.size() / 2 && i.hasNext(); count++) {
            left.add(i.next());
        }
        while (i.hasNext()) {
            right.add(i.next());
        }

        // Sort recursively.
        Future<ArrayList<T>> leftFuture = executor.submit(() -> doSort(left));
        Future<ArrayList<T>> rightFuture = executor.submit(() -> doSort(right));

        ArrayList<T> sortedLeft = null;
        ArrayList<T> sortedRight = null;

        while (sortedLeft == null || sortedRight == null) {
            if (leftFuture.isDone()) {
                sortedLeft = leftFuture.get();
            }
            if (rightFuture.isDone()) {
                sortedRight = rightFuture.get();
            }
            Thread.sleep(10);
        }

        // Merge
        ArrayList<T> result = new ArrayList<>(collection.size());
        while (!sortedLeft.isEmpty() || !sortedRight.isEmpty()) {
            if (sortedLeft.isEmpty()) {
                result.addAll(sortedRight);
                break;
            }
            if (sortedRight.isEmpty()) {
                result.addAll(sortedLeft);
                break;
            }
            T first = (left.get(0).compareTo(sortedRight.get(0)) <= 0) ? sortedLeft.remove(0) : sortedRight.remove(0);
            result.add(first);
        }
        return result;
    }
}
