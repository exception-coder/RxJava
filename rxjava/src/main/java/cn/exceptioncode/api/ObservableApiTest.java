package cn.exceptioncode.api;

import io.reactivex.Observable;
import org.junit.Test;

import java.util.Random;

public class ObservableApiTest {

    private final String[] monthArray = {"Jan", "Feb", "Mar", "Apl", "Maly", "Jun", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    @Test
    public void tets() {
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
        .subscribe(item -> System.out.println("we got: "+item.toString()+" from the Observable"),throwable -> System.out.println("异常 -> "+throwable.getMessage() ),()->System.out.println("Emission completed"));


    }
}
