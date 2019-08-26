## 第二章 在 RxJava 中创建 Observable 

- ### 初识 Observable
  - #### Hello World
    ```java
    public class ObservableTest {
            @Test
            public void observableCreateTest(){
                
                // 定义数据源生产行为
                Observable<Object> observable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter emitter) throws Exception {
                        emitter.onNext("处理的数字是：" + Math.random() * 100);
                        emitter.onComplete();
                    }
                });
        
                observable.subscribe(comsumer -> {
                    System.out.println("我处理的元素是：" + comsumer);
                });
        
                // 添加订阅并定义 `onNext` 消费动作
                observable.subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        System.out.println("我处理的元素是：" + o);
                    }
                });
            }
    }
    ```
  - #### 源码解析
    - ##### 定义数据源生产行为
    以下是关键代码，可以看出，第一步仅仅是创建了一个 `Observable` 实例 `ObservableCreate` 并绑定了我们传递的数据源实现 `ObservableOnSubscribe`
    ```java
    
    public abstract class Observable<T> implements ObservableSource<T> {
    
        // 传递我们创建的 `source` 实例进行增强包装 返回 `ObservableCreate` 实例
        public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
            return RxJavaPlugins.onAssembly(new ObservableCreate<T>(source));
        }
    
    }
    
    public final class ObservableCreate<T> extends Observable<T> {
        final ObservableOnSubscribe<T> source;
    
        public ObservableCreate(ObservableOnSubscribe<T> source) {
            this.source = source;
        }
    }
    
    ```
    - ##### 添加订阅并定义消费动作
    ```java
    
    public abstract class Observable<T> implements ObservableSource<T> {
    
    
        public final Disposable subscribe(Consumer<? super T> onNext) {
            return subscribe(onNext, Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer());
        }
    
    
        public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
                                          Action onComplete, Consumer<? super Disposable> onSubscribe) {
            // 传递我们创建的 `onNext` 实例进行增强包装 返回 `LambdaObserver` 实例
            LambdaObserver<T> ls = new LambdaObserver<T>(onNext, onError, onComplete, onSubscribe);
            subscribe(ls);
            return ls;
        }
    
    
        public final void subscribe(Observer<? super T> observer) {
            observer = RxJavaPlugins.onSubscribe(this, observer);
            // 调用具体实例的 `subscribeActual` 方法 ，因为我们创建的是 `ObservableCreate` 对象，所以此处调用的是 `ObservableCreate` 的实现。
            subscribeActual(observer);
        }
    
    }
    
    public final class ObservableCreate<T> extends Observable<T> {
        final ObservableOnSubscribe<T> source;
    
        public ObservableCreate(ObservableOnSubscribe<T> source) {
            this.source = source;
        }
    
        protected void subscribeActual(Observer<? super T> observer) {
            // `LambdaObserver` 持有我们的 `onNext` 动作行为，此处再进行一次包装
            CreateEmitter<T> parent = new CreateEmitter<T>(observer);
            // 调用 `LambdaObserver::onSubscribe` 方法 因为我们没有定义 所以使用默认实现  `Functions.emptyConsumer()`
            observer.onSubscribe(parent);
    
            // 此时调用我们定义的数据源实现实例的 `subscribe` 并传递我们的包装对象 `parent` 
            // 所以在调用 `parent::onNext` 其实就是调用 `lambdaObserver::onNext` 最终还是调用我们的定义的 `onNext` 消费动作
            source.subscribe(parent);
        }
    
        static final class CreateEmitter<T>
                extends AtomicReference<Disposable>
                implements ObservableEmitter<T>, Disposable {
    
            final Observer<? super T> observer;
    
            CreateEmitter(Observer<? super T> observer) {
                this.observer = observer;
            }
    
            @Override
            public void onNext(T t) {
                if (t == null) {
                    return;
                }
                if (!isDisposed()) {
                    observer.onNext(t);
                }
            }
    
    
            @Override
            public void onComplete() {
                if (!isDisposed()) {
                    try {
                        observer.onComplete();
                    } finally {
                        dispose();
                    }
                }
            }
    
    
        }
    
    }
    
    public final class LambdaObserver<T> extends AtomicReference<Disposable>
            implements Observer<T>, Disposable, LambdaConsumerIntrospection {
    
        public LambdaObserver(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
                              Action onComplete,
                              Consumer<? super Disposable> onSubscribe) {
            super();
            this.onNext = onNext;
            this.onError = onError;
            this.onComplete = onComplete;
            this.onSubscribe = onSubscribe;
        }
    
    
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                onSubscribe.accept(this);
            }
        }
    
        public void onNext(T t) {
            if (!isDisposed()) {
                try {
                    onNext.accept(t);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    get().dispose();
                    onError(e);
                }
            }
        }
    
    
        public void onComplete() {
            if (!isDisposed()) {
                lazySet(DisposableHelper.DISPOSED);
                onComplete.run();
            }
        }
    }
    
    ```
- ### Observable 无限流
- ### Observable 错误处理