package cn.exceptioncode.第2章_在RxJava中创建Observable._2_2_2_subscribe的二三事;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.junit.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class ObservableTest {

    public static void main(String[] args) {

    }


    /**
     * 初识 Observable
     */
    @Test
    public void observableCreateTest() {
        /**
         *
         * 1.1 传入 ObservableOnSubscribe 实例
         * 1.2 创建 ObservableCreate 对象时候传递 ObservableOnSubscribe 实例
         * 1.3 返回 ObservableCreate 实例
         *
         */
        // 创建被观察者
        Observable<Object> observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("处理的数字是：" + Math.random() * 100);
                emitter.onComplete();
            }
        });


        /**
         *
         * 1.1 创建一个 LambdaObserver 对象实例
         * 1.2 调用 observable::subscribeActual 传递 LambdaObserver 实例参数
         * 1.x 返回一个 LambdaObserver 对象实例
         *
         */
        // 添加订阅者1(一个包含 onNext,  onError, onComplete,  onSubscribe 方法的观察者)
        observable.subscribe(comsumer -> {
            System.out.println("我处理的元素是：" + comsumer);
        });

        // 添加订阅者2
        observable.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("我处理的元素是：" + o);
            }
        });
    }


    @Test
    public void observableCacheTest() {
        Observable<Object> observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("处理的数字是：" + Math.random() * 100);
                emitter.onComplete();
            }
        }).cache();


        observable.subscribe(comsumer -> {
            System.out.println("我处理的元素是：" + comsumer);
        });

        observable.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.println("我处理的元素是：" + o);
            }
        });
    }

    @Test
    public void observableInfiniteTest() {
    }

    static void error(int n) {
        if (n == 5) throw new RuntimeException("n==5 异常");
        System.out.println("我消费的元素是：" + n);
    }

    static Integer errorP(int n) {
        if (n == 5) throw new RuntimeException("n==5 异常");
        System.out.println("我消费的元素是：" + n);
        return n;
    }


    @Test
    public void observableErrorTest() {
        Observable.create(observer -> {
            try {
                observer.onNext(11);
                observer.onComplete();
            } catch (Exception e) {
                observer.onError(e);
            }
        }).subscribe(data -> error((int) data), Throwable::printStackTrace, () ->
                System.out.println("Emission completed")
        );
        System.out.println("******************************************************************************************");
        Observable.fromCallable(() -> errorP(11)).subscribe(data -> System.out.println("消费元素:" + data), Throwable::printStackTrace, () -> System.out.println("Emission completed"));
    }

    @Test
    public void infiniteUnsubscribeCacheThreadTest() {
        Observable<Object> observable = Observable.create(observer -> {
            Runnable runnable = () -> {
                BigInteger i = BigInteger.ZERO;
                while (!observer.isDisposed()) {
                    observer.onNext(i);
                    i = i.add(BigInteger.ONE);
                    System.out.println(Thread.currentThread().getName() + " 下一个消费的数字 " + i.toString());
                }
            };
            new Thread(runnable).start();
        });

        final Disposable disposable1 = observable.subscribe(data -> {
            System.out.println(Thread.currentThread().getName() + " 观察者1 " + data.toString());
        });
        final Disposable disposable2 = observable.subscribe(data -> {
            System.out.println(Thread.currentThread().getName() + " 观察者2 " + data.toString());
        });

        try{
            TimeUnit.MILLISECONDS.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        disposable1.dispose();
        disposable2.dispose();
        System.out.println("我取消了订阅");

        try{
            TimeUnit.MILLISECONDS.sleep(5000);
            System.out.println("程序结束");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
