package map;

import java.util.HashMap;

/**
 * created by lanxinghua@2dfire.com on 2020/4/27
 */
public class HashMapTest {
    public static void main(String[] args) {
        int result = compute(1, value -> value * value);
        System.out.println(result);
    }

    public static int compute(int a, Function<Integer, Integer> function){
        Integer result = function.apply(a);
        return result;
    }
}



interface Function<T, R> {
    R apply(T t);
}

