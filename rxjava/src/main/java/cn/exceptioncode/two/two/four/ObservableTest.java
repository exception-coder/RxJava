package cn.exceptioncode.two.two.four;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 泡泡熊
 * @date 2019/7/21 3:46
 */
public class ObservableTest {

    @Test
    public void hotObservable() {
        Observable<Object> observable = Observable.create(
                new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter emitter) throws Exception {
                        emitter.onNext("处理的数字输： " + Math.random() * 100);
                        emitter.onComplete();
                    }
                }
//                emitter -> {
//                    emitter.onNext("处理的数字输： "+Math.random() * 100);
//                    emitter.onComplete();
//                }
        ).cache();

        observable.subscribe(consumer -> {
            System.out.println("我处理的元素是：" + consumer);
        });

        observable.subscribe(consumer -> {
            System.out.println("我处理的元素是：" + consumer);
        });
    }


    @Test
    public void infiniteTest() {
        Observable<Object> observable = Observable.create(
                emitter -> {
                    BigInteger i = BigInteger.ZERO;
                    while (true) {
                        emitter.onNext(i);
                        i = i.add(BigInteger.ONE);
                        if (i.compareTo(BigInteger.valueOf(10000L)) > 0) {
                            break;
                        }
                    }
                }
        ).cache();
        observable.subscribe(x -> System.out.println(Thread.currentThread().getName() + ":" + x));
    }


    @Test
    public void infiniteThreadTest() {
        Observable<Object> observable = Observable.create(
                emitter -> {
                    Runnable runnable = () -> {
                        BigInteger i = BigInteger.ZERO;
                        while (true) {
                            emitter.onNext(i);
                            i = i.add(BigInteger.ONE);
                            if (i.compareTo(BigInteger.valueOf(10000L)) > 0) {
                                break;
                            }
                        }
                    };

                    new Thread(runnable).start();
                }
        ).cache();
        observable.subscribe(
                new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println(Thread.currentThread().getName() + ":" + d);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );
        observable.subscribe(x -> System.out.println(Thread.currentThread().getName() + ":" + x));
        observable.subscribe(x -> System.out.println(Thread.currentThread().getName() + ":" + x));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void poolPushCollection() {
        List<Integer> integerList = new ArrayList(10);
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        try {
           Observable<Integer> observable =  Observable.create(observer -> {
                AtomicInteger atomicInteger = new AtomicInteger(integerList.size());
                forkJoinPool.submit(() -> {
                    integerList.forEach(id -> {
                        observer.onNext(id);
                        if (atomicInteger.decrementAndGet() == 0) {
                            observer.onComplete();
                        }
                    });
                });
            });
           observable.subscribe(id -> System.out.println(Thread.currentThread().getName() + "：我是订阅者" + id));


        } finally {
            try {
                forkJoinPool.shutdown();
                int shutdownDelaySec = 1;
                forkJoinPool.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                List<Runnable> l = forkJoinPool.shutdownNow();
                System.out.println("还剩：" + l.size() + " 个任务等待执行，服务已关闭");
            }
        }
    }
}
