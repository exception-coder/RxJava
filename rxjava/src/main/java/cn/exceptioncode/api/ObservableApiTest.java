package cn.exceptioncode.api;

import io.reactivex.Observable;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ObservableApiTest {

    private final String[] monthArray = {"Jan", "Feb", "Mar", "Apl", "May", "Jun", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    private final Integer[] numbers = {1, 2, 13, 34, 15, 17};

    private final String[] fruits = {"苹果", "梨", "李子", "荔枝", "芒果"};


    @Test
    public void test() {
        Observable<String> observable = Observable.fromArray(monthArray);
        observable = observable.map(String::toUpperCase);
        observable.subscribe(System.out::println);

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(i -> 1 % 3 > 0).map(i -> "#" + i * 10)
                .filter(s -> s.length() < 4)
                .subscribe(System.out::println);
        System.out.println("==========================================================================================");
        int id = new Random().nextInt(100);
        System.out.println(id);
        Observable.just(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L).map(item -> {
            if ((id % 2) == 1) {
                return item;
            } else {
                throw new RuntimeException("%2 !=1 异常");
            }
        }).flatMap(bytes -> {
            System.out.println(bytes);
            return Observable.empty();
        }, e -> Observable.just(e.getMessage()), () -> Observable.just(id))
                .subscribe(item -> System.out.println("we got: " + item.toString() + " from the Observable"), throwable -> System.out.println("异常 -> " + throwable.getMessage()), () -> System.out.println("Emission completed"));
    }


    /**
     * 分组操作
     */
    @Test
    public void groupByTest() {
        Observable.fromArray(monthArray).groupBy(item -> item.length() <= 3 ? "THREE"
                : item.length() < 6 ? ">4" : "DEFAULT").subscribe(observable -> {
            System.out.println("***************************************************************************************");
            System.out.println(observable);
            observable.subscribe(item ->
                    System.out.println(Thread.currentThread().getName() + ":" + observable.getKey() + ":" + item)
            );
        });
    }

    /**
     * 数据源合并操作
     */
    @Test
    public void mergTest() {
        Observable.merge(Observable.range(1, 5), Observable.range(10, 3))
                .subscribe(item -> System.out.println("we got: " + item + " from the Observable"),
                        throwable -> System.out.println(throwable.getMessage()), () -> System.out.println("Emission completed"));
    }


    /**
     * zip合并操作 获取不到元素则会跳过处理
     */
    @Test
    public void zipTest() {
        Observable.zip(Observable.fromArray(numbers), Observable.fromArray(fruits), (obj1, obj2) -> obj1 + "个" + obj2).subscribe(item -> {
            System.out.println(item);
        });
    }

    @SneakyThrows
    @Test
    public void combineLatestTest() {
        Observable.combineLatest(Observable.fromArray(numbers), Observable.fromArray(fruits), (obj1, obj2) -> obj1 + "个" + obj2).subscribe(item -> {
            System.out.println(item);
        });

        Observable.combineLatest(Observable.interval(2, TimeUnit.SECONDS).map(x -> "Java" + x),
                Observable.interval(1, TimeUnit.SECONDS).map(x -> "Spring" + x),
                (obj1, obj2) -> obj1 + ":" + obj2).subscribe(item -> {
            System.out.println(item);
        });

        Thread.sleep(6 * 1000);
    }

    @SneakyThrows
    @Test
    public void withLatestFromTest() {
        Observable.fromArray(numbers).withLatestFrom(Observable.fromArray(fruits), (item1, item2) -> item1 + "个" + item2).forEach(System.out::println);
        System.out.println("******************************************************************************************");
        Observable observable1 = Observable.interval(2, TimeUnit.SECONDS).map(x -> "Java" + x);
        Observable observable2 = Observable.interval(1, TimeUnit.SECONDS).map(x -> "Spring" + x);
        observable1.withLatestFrom(observable2, (s, f) -> s + ":" + f).forEach(System.out::println);
        Thread.sleep(6 * 1000);
    }

    @Test
    public void ambWithTest() {
    }


}
