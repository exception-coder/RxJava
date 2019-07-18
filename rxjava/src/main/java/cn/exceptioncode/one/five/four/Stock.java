package cn.exceptioncode.one.five.four;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stock {
    private final Map<Product,StockItem> stockItemMap = new ConcurrentHashMap();

    private StockItem getItem(Product product){
        stockItemMap.putIfAbsent(product,new StockItem());
        return stockItemMap.get(product);
    }

    public void store(Product product,long amount){
        getItem(product).store(amount);
    }

    public void remove(Product product,long amount) throws Exception{
        if(getItem(product).remove(amount)!=amount)
            throw new Exception(product.toString());
    }
}
