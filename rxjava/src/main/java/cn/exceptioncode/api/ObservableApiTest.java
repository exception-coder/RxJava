package cn.exceptioncode.api;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableFromArray;
import org.junit.Test;

public class ObservableApiTest {

    private final String[] monthArray = {"Jan","Feb","Mar","Apl","Maly","Jun","July","Aug","Sept","Oct","Nov","Dec"};

    @Test
    public void tets(){
        Observable<String> observable =  Observable.fromArray(monthArray);
        System.out.println(observable);
        observable = observable.map(String::toUpperCase);
        System.out.println(observable);
    }
}
