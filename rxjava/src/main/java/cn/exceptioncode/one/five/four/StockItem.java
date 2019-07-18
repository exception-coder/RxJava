package cn.exceptioncode.one.five.four;

import java.util.concurrent.atomic.AtomicLong;

public class StockItem {

    private final AtomicLong amountItemStock = new AtomicLong(0);

    public void store(long n){
        amountItemStock.accumulateAndGet(n,(pre,mount) -> pre+mount);
    }

    public long remove(long n){
        class RemoveData{
            long remove;
        }
        RemoveData removeData = new RemoveData();
        amountItemStock.accumulateAndGet(n,(pre,mount) -> pre >=n ?
                pre - (removeData.remove = mount):pre - (removeData.remove = 0L));
        return removeData.remove;
    }
}
