package cn.exceptioncode.consume;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.LongStream;

public class ConsumeSubmissionPublisher {

    public static void main(String[] args) throws InterruptedException, ExecutionException{
        publish();
    }

    public static void publish()throws InterruptedException, ExecutionException{
        CompletableFuture future = null;
        try(SubmissionPublisher publisher = new SubmissionPublisher<Long>()){
            System.out.println("subscriber Buffer Size:"+publisher.getMaxBufferCapacity());
            future = publisher.consume(System.out::println);
            LongStream.range(1,10).forEach(publisher::submit);
        }finally {
            future.get();
        }
    }
}
