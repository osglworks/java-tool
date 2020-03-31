package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2020 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.osgl.TestBase;

import java.util.*;

public class LFUCacheTest extends TestBase {

    @Test
    public void testLFUCache() {
        LFUCache<Integer, Integer> cache;
        try {
            cache = new LFUCache<Integer, Integer>(6, 2);
            fail( "Failed to throw exception!" );
        } catch (IllegalArgumentException e) {}
        try {
            cache = new LFUCache<Integer, Integer>(-1, 0.2);
            fail( "Failed to throw exception!" );
        } catch (IllegalArgumentException e) {}
        try {
            cache = new LFUCache<Integer, Integer>(3, -0.2);
            fail( "Failed to throw exception!" );
        } catch (IllegalArgumentException e) {}
        cache = new LFUCache<Integer, Integer>(10, 0.2);
        cache.set(1, 1);
        LFUCache<String, String> cache2 = new LFUCache<String, String>(4, 0.5);
        assert(cache2.get("5") == null);
        cache2.set("1", "1");
        assert("1" == cache2.get("1"));
    }

    @Test
    public void testGet() {
        int cap = 4;
        double num = 0.5;
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(cap, num);
        assert(cache.get(5) == null);
        cache.set(1, 1);
        assert(1 == cache.get(1));
    }

    @Test
    public void testSet() {
        int cap = 4;
        double num = 0.5;
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(cap, num);
        cache.set(1, 1);
        assert(1 == cache.get(1));
        cache.set(1, 2);
        assert(2 == cache.get(1));
    }

    @Test
    public void testMget() {
        int cap = 4;
        double num = 0.5;
        LFUCache<Integer, Integer> cache = new LFUCache<>(cap, num);
        Random rand = new Random();
        int m = rand.nextInt(10) + 1;
        List<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < m; ++i) {
            int key = rand.nextInt(20);
            keys.add(key);
        }
        Map<Integer, Integer> kv = cache.mget(keys);
        for (Map.Entry<Integer, Integer> p : kv.entrySet()) {
            assert(p.getValue() == null);
        }
        cache.set(keys.get(0), 5);
        kv = cache.mget(keys);
        assert(kv.get(keys.get(0)) == 5);
    }

    @Test
    public void testMset() {
        Random rand = new Random();
        int m = 5;
        Map<Integer, Integer> kv = new HashMap<>(), ret;
        List<Integer> keys = new ArrayList<Integer>();
        Integer key = rand.nextInt(10);
        for (int i = 0; i < m; ++i) {
            keys.add(key + i);
            kv.put(key + i, rand.nextInt(100));
        }
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(6, 0.5);
        cache.mset(kv);
        ret = cache.mget(keys);
        assert(ret.size() == kv.size());
        for (Map.Entry<Integer, Integer> entry : kv.entrySet()) {
            eq(entry.getValue(), ret.get(entry.getKey()));
        }
    }

    @Test
    public void testIncr() {
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(6, 0.5);
        assert(cache.get(5) == null);
        cache.incr(5, 5);
        assert(cache.get(5) == 5);
        cache.incr(5, -5);
        assert(cache.get(5) == 0);
    }

    @Test
    public void testDecr() {
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(6, 0.5);
        assert(cache.get(5) == null);
        cache.decr(5, 5);
        assert(cache.get(5) == -5);
        cache.decr(5, -5);
        assert(cache.get(5) == 0);
    }

    @Test
    public void testTouchEvict() {
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(2, 0.5);
        assert(cache.get(1) == null);
        cache.set(1, 1);
        assert(cache.get(1) == 1);
        cache.set(2, 2);
        cache.set(3, 3);  // evict (2, 2)
        assert(cache.get(2) == null);
        assert(cache.get(1) == 1);
        cache.set(2, 2);  // evict (3, 3)
        assert(cache.get(3) == null);
        assert(cache.get(2) == 2);
        // increasing frequency
        cache.set(1, 2);
        cache.set(1, 3);
        cache.set(1, 4);
        assert(cache.get(1) == 4);
        cache.set(3, 5);  // evict (2, 2)
        assert(cache.get(2) == null);
        assert(cache.get(1) == 4);
        assert(cache.get(3) == 5);
    }

    public static int getNextOp(boolean isManual, Scanner sc, Random rand) {
        if (isManual) {
            System.out.print("Chooes (0) GET (1) SET (2) MSET (3) MGET; (4) INCR; (5) DECR\nYour choice(0-5): ");
            return sc.nextInt();
        }
        return rand.nextInt(6);
    }

    public static int getNextKey(boolean isManual, Scanner sc, Random rand) {
        if (isManual) {
            System.out.print("Input Key: ");
            return sc.nextInt();
        }
        return rand.nextInt(10);
    }

    public static int getNextValue(boolean isManual, Scanner sc, Random rand) {
        if (isManual) {
            System.out.print("Input Value: ");
            return sc.nextInt();
        }
        return rand.nextInt(200) - 100;
    }

    public static int getNextDelta(boolean isManual, Scanner sc, Random rand) {
        if (isManual) {
            System.out.print("Input Delta: ");
            return sc.nextInt();
        }
        return rand.nextInt(200) - 100;
    }

    public static void main(String[] args) {
        int op, key, val, cap;
        Scanner sc = new Scanner(System.in);
        System.out.println("Setting the capacity of cache:");
        cap = sc.nextInt();
        System.out.println("Setting the percentage of objects for replacement:");
        double num = sc.nextDouble();
        System.out.println("Choose (0) test manually or (1) test randomly: ");
        boolean manual = sc.nextInt() == 0;

        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(cap, num);
        Random rand = new Random();
        int n = 10000;
        sc.nextLine();
        while (n >= 0) {
            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
            --n;
            op = getNextOp(manual, sc, rand);
            if (op == 0) {
                key = getNextKey(manual, sc, rand);
                System.out.print("Fetching: " + key + " :");
                try {
                    System.out.println(cache.get(key));
                } catch (RuntimeException e) {
                    System.out.println(" doesn't exist!");
                }
            }
            else if (op == 1) {
                key = getNextKey(manual, sc, rand);
                val = getNextValue(manual, sc, rand);
                System.out.println("Insert: " + key + ", " + val);
                cache.set(key, val);
            }
            else if (op == 2) {
                int m = rand.nextInt(10) + 1;
                if (manual) {
                    System.out.println("Number of pairs to set:");
                    m = sc.nextInt();
                }
                Map<Integer, Integer> kv = new HashMap<>();
                System.out.print("MSET: " + m + " pairs: ");
                for (int i = 0; i < m; ++i) {
                    key = getNextKey(manual, sc, rand);
                    val = getNextValue(manual, sc, rand);
                    System.out.print("(" + key + ", " + val + "), ");
                    kv.put(key, val);
                }
                cache.mset(kv);
                System.out.println("");
            }
            else if (op == 3) {
                int m = rand.nextInt(10) + 1;
                if (manual) {
                    System.out.println("Number of pairs to get:");
                    m = sc.nextInt();
                }
                List<Integer> keys = new ArrayList<Integer>();
                System.out.print("MGET: ");
                for (int i = 0; i < m; ++i) {
                    key = getNextKey(manual, sc, rand);
                    System.out.print(key + ", ");
                    keys.add(key);
                }
                System.out.print("\nReturn:");
                Map<Integer, Integer> kv = cache.mget(keys);
                for (Map.Entry<Integer, Integer> p : kv.entrySet()) {
                    System.out.print("(" + p.getKey() + ", " + p.getValue() + "), ");
                }
                System.out.println("");
            }
            else if (op == 4) {
                key = getNextKey(manual, sc, rand);
                int d = getNextDelta(manual, sc, rand);
                System.out.println("INCR: " + key + " by " + d);
                cache.incr(key, d);
            }
            else if (op == 5) {
                key = getNextKey(manual, sc, rand);
                int d = getNextDelta(manual, sc, rand);
                System.out.println("DECR: " + key + " by " + d);
                cache.decr(key, d);
            }
            cache.print();
        }
        sc.close();
    }
}
