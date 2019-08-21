package cn.exceptioncode.第2章_在RxJava中创建Observable._2_2_2_subscribe的二三事;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class ObservableDemo {

    public static void main(String[] args) {

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

}
