package com.company.users.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimeUtilsTest {

    @Test
    void filterPrimes_returnsEmptyList_whenInputIsNull() {
        List<Integer> result = PrimeUtils.filterPrimes(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterPrimes_returnsEmptyList_whenInputIsEmpty() {
        List<Integer> result = PrimeUtils.filterPrimes(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterPrimes_filtersOnlyPrimes_inMixedList() {
        List<Integer> input = Arrays.asList(-1, 0, 1, 2, 3, 4, 5, 6, 7, null, 8, 9, 10, 11);
        List<Integer> result = PrimeUtils.filterPrimes(input);
        assertEquals(Arrays.asList(2, 3, 5, 7, 11), result);
    }

    @Test
    void isPrime_basicChecks() {
        assertFalse(PrimeUtils.isPrime(-10));
        assertFalse(PrimeUtils.isPrime(0));
        assertFalse(PrimeUtils.isPrime(1));
        assertTrue(PrimeUtils.isPrime(2));
        assertTrue(PrimeUtils.isPrime(3));
        assertFalse(PrimeUtils.isPrime(4));
        assertTrue(PrimeUtils.isPrime(5));
        assertTrue(PrimeUtils.isPrime(97));
        assertFalse(PrimeUtils.isPrime(99));
    }
}
