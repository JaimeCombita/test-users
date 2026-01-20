package com.company.users.utils;

import java.util.ArrayList;
import java.util.List;

public final class PrimeUtils {

    private PrimeUtils() {}

    public static List<Integer> filterPrimes(List<Integer> numbers) {
        List<Integer> result = new ArrayList<>();
        if (numbers == null || numbers.isEmpty()) {
            return result;
        }
        for (Integer number : numbers) {
            if (number != null && isPrime(number)) {
                result.add(number);
            }
        }
        return result;
    }

    public static boolean isPrime(int number) {
        if (number < 2) return false;
        if (number % 2 == 0) return number == 2;
        if (number % 3 == 0) return number == 3;
        int i = 5;
        while (i * i <= number) {
            if (number % i == 0 || number % (i + 2) == 0) return false;
            i += 6;
        }
        return true;
    }
}
