package aud.aud2;
import java.util.function.*;

public class FunctionalInterfacesDemo {
    public static void main(String[] args) {
        Function<String, Integer> stringLen = str -> str.length();
        System.out.println("Length of 'Hello': " + stringLen.apply("Hello"));
        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a+b;
        System.out.println(sum.apply(5,3));
        Predicate<Integer> isEven = num -> num%2 == 0;
        System.out.println(isEven.test(4));
        Consumer<String> printString = str -> System.out.println("Printing: " + str);
        printString.accept("hello bro ");
        Supplier<Long> currentTimeMillis = () -> System.currentTimeMillis();
        System.out.println(currentTimeMillis.get());
    
    }
}
