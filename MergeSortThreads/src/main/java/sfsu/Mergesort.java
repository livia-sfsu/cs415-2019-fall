package sfsu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Mergesort<T extends Comparable> {

    public static <T extends Comparable> Collection<T> sort(Collection<T> collection) {
        Worker worker = new Worker(new ArrayList<>(collection));
        try {
            worker.start();
            worker.join();
        } catch (InterruptedException ie) {
            System.err.println("I failed at threads. What went wrong?");
            System.exit(-1);
        }
        return worker.collection;
    }

    static class Worker <T extends Comparable> extends Thread {
        ArrayList<T> collection;
        Worker(ArrayList<T> collection) {
            this.collection = collection;
        }

        @Override
        public void run() {
            try {
                collection = doSort(collection);
            } catch (InterruptedException ie) {
                System.err.println("I failed at threads. What went wrong?");
                System.exit(-1);
            }
        }
    }

    /**
     * Recursive implementation of mergesort.
     * @param collection
     * @return
     */
    private static <T extends Comparable> ArrayList<T> doSort(ArrayList<T> collection) throws InterruptedException {
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
        ArrayList<T> left = new ArrayList<>(collection.size()/2);
        ArrayList<T> right = new ArrayList<>(collection.size()/2);

        Iterator<T> i = collection.iterator();
        for (int count = 0; count < collection.size() / 2 && i.hasNext(); count++) {
            left.add(i.next());
        }
        while (i.hasNext()) {
            right.add(i.next());
        }

        // Sort recursively.
        Worker leftWorker = new Worker(left);
        Worker rightWorker = new Worker(right);

        leftWorker.start();
        rightWorker.start();

        leftWorker.join();
        rightWorker.join();

        left = leftWorker.collection;
        right = rightWorker.collection;

        // Merge
        ArrayList<T> result = new ArrayList<>(collection.size());
        while (!left.isEmpty() || !right.isEmpty()) {
            if (left.isEmpty()) {
                result.addAll(right);
                break;
            }
            if (right.isEmpty()) {
                result.addAll(left);
                break;
            }
            T first = (left.get(0).compareTo(right.get(0)) <= 0) ? left.remove(0) : right.remove(0);
            result.add(first);
        }
        return result;
    }
}
