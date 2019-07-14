package cn.exceptioncode;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class DockerXDemoPublisher<T> implements Flow.Publisher<T>, AutoCloseable {
    private final ExecutorService executor;
    private CopyOnWriteArrayList<DockerXDemoSubscription> list = new CopyOnWriteArrayList();

    public DockerXDemoPublisher(ExecutorService executor) {
        this.executor = executor;
    }


    public void submit(T item) {
        System.out.println("********* 开始发布元素 item: " + item + "*********");
        list.forEach(e -> {
            e.future = executor.submit(() -> {
                e.subscriber.onNext(item);
            });
        });
    }

    @Override
    public void close() throws Exception {
        list.forEach(e -> {
            e.future = executor.submit(() -> {
                e.subscriber.onComplete();
            });
        });

    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {

    }

    static class DockerXDemoSubscription<T> implements Flow.Subscription {

        private final Flow.Subscriber<? super T> subscriber;
        private final ExecutorService executor;
        private Future<?> future;
        private T item;
        private boolean completed;


        public DockerXDemoSubscription(Flow.Subscriber<? super T> subscriber, ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;
        }

        @Override
        public void request(long n) {

            if (n != 0 && !completed) {
                if (n < 0) {
                    IllegalArgumentException ex = new IllegalArgumentException();
                    executor.execute(() -> subscriber.onError(ex));
                } else {
                    future = executor.submit(() -> {
                        subscriber.onNext(item);
                    });
                }
            } else {
                subscriber.onComplete();
            }


        }

        @Override
        public void cancel() {
            completed = true;
            if (future != null && !future.isCancelled()) {
                this.future.cancel(true);
            }
        }
    }

    private static void demoSubscribe(DockerXDemoPublisher<Integer> publisher, String subscriberName) {
        DockerXDemoSubscriber<Integer> subscriber = new DockerXDemoSubscriber(subscriberName, 4l);
        publisher.subscribe(subscriber);
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = ForkJoinPool.commonPool();
        try (DockerXDemoPublisher<Integer> publisher = new DockerXDemoPublisher<>(executorService)) {
            demoSubscribe(publisher, "One");
            demoSubscribe(publisher, "Two");
            demoSubscribe(publisher, "Three");
            IntStream.range(1, 5).forEach(publisher::submit);
        } finally {

        }
    }


}
