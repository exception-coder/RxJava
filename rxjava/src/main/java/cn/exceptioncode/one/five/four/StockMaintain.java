package cn.exceptioncode.one.five.four;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

public class StockMaintain implements Flow.Subscriber<Order> {

    private Stock stock;
    private Flow.Subscription subscription = null;
    private ExecutorService executorService = ForkJoinPool.commonPool();

    public StockMaintain(Stock stock) {
        stock = new Stock();
        this.stock = stock;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("*********调用 onSubscription*********");
        subscription.request(3);
        this.subscription = subscription;
    }

    @Override
    public void onNext(Order item) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
