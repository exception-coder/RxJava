package cn.exceptioncode.Java9响应式编程入门Demo;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 *
 *
 * @param <T>
 */
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
        // 调用消费者的 onSubscribe 方法，传递 Subscription
        subscriber.onSubscribe(new DockerXDemoSubscription(subscriber,executor));
        list.add(new DockerXDemoSubscription(subscriber,executor));
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
        // 创建一个消费者
        DockerXDemoSubscriber<Integer> subscriber = new DockerXDemoSubscriber(subscriberName, 4l);
        // 调用消费者的 onSubscribe 方法，传递 Subscription
        // list 集合中添加一个 subscription
        publisher.subscribe(subscriber);
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = ForkJoinPool.commonPool();
        // 创建一个生产者
        try (DockerXDemoPublisher<Integer> publisher = new DockerXDemoPublisher<>(executorService)) {
            demoSubscribe(publisher, "One");
            demoSubscribe(publisher, "Two");
            demoSubscribe(publisher, "Three");
            IntStream.range(1, 5).forEach(publisher::submit);
        } finally {
            try{
                executorService.shutdown();
                int shutdownDelaySec = 1;
                System.out.println(".........等待"+ shutdownDelaySec+" 秒后结束服务.........");
                executorService.awaitTermination(shutdownDelaySec,TimeUnit.SECONDS);
            }catch (Exception e){
                System.out.println("捕获到 executorService.awaitTermination()方法的异常： "+ e.getClass().getName());
            }finally {
                System.out.println("调用 executorService.shutdownNow()结束服务...");
                List<Runnable> l = executorService.shutdownNow();
                System.out.println("还剩 "+l.size()+" 个任务等待执行，服务已关闭 ");
            }

        }
    }


}
