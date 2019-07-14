package cn.exceptioncode;

import java.util.concurrent.Flow;

public class DockerXDemoSubscriber<T> implements Flow.Subscriber<T> {
    private String name;
    private Flow.Subscription subscription;
    final long bufferSize;
    long count;


    public String getName() {
        return name;
    }

    public Flow.Subscription getSubscription() {
        return subscription;
    }

    public DockerXDemoSubscriber(String name, long bufferSize) {
        this.name = name;
        this.bufferSize = bufferSize;
    }

    public void onSubscribe(Flow.Subscription subscription) {
        (this.subscription = subscription).request(bufferSize);
        System.out.println("开始 onSubscribe 订阅");
        try{
            Thread.sleep(100);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void onNext(Object item) {
        System.out.printf("%s %s name: %s item: %s %s","#####",name,item);
        System.out.println(name+" received: "+ item);
        try{
            Thread.sleep(10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onComplete() {
        System.out.println("Completed");
    }

}
