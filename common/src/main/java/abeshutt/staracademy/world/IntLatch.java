package abeshutt.staracademy.world;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntPredicate;

public class IntLatch {

    private CountDownLatch latch;

    public IntLatch() {
        this(0);
    }

    private IntLatch(int count) {
        this.latch = new CountDownLatch(count);
    }

    public synchronized int getCount() {
        return (int)this.latch.getCount();
    }

    public synchronized void decrement() {
        this.latch.countDown();
        this.notifyAll();
    }

    public synchronized void increment() {
        this.latch = new CountDownLatch((int)this.latch.getCount() + 1);
        this.notifyAll();
    }

    public synchronized void waitUntil(IntPredicate predicate) throws InterruptedException {
        while(!predicate.test(this.getCount())) {
            this.wait();
        }
    }

}
