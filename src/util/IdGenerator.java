package util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public static String next(String prefix) {
        int n = counters.computeIfAbsent(prefix, k -> new AtomicInteger(1)).getAndIncrement();
        return prefix + "-" + n;
    }
}
