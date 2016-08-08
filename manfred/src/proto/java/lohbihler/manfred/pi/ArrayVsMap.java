package lohbihler.manfred.pi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayVsMap {
    public static void main(String[] args) {
        list();
        map();
        array();

        list();
        map();
        array();

        list();
        map();
        array();
    }

    static final int size = 20;
    static final int writes = 1000000;
    static final int reads = 10000000;

    static void array() {
        float[] arr = new float[size];

        long start = System.currentTimeMillis();
        float value = 0.1F;
        int index = 0;
        for (int i = 0; i < writes; i++) {
            arr[index++] = value;
            if (index >= size) {
                index = 0;
                value += 0.1F;
            }
        }
        System.out.println("arr.write: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        index = 0;
        for (int i = 0; i < reads; i++) {
            float f = arr[index];
        }
        System.out.println("arr.read: " + (System.currentTimeMillis() - start));
    }

    static void list() {
        List<Float> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
            list.add(0F);

        long start = System.currentTimeMillis();
        float value = 0.1F;
        int index = 0;
        for (int i = 0; i < writes; i++) {
            list.set(index++, value);
            if (index >= size) {
                index = 0;
                value += 0.1F;
            }
        }
        System.out.println("list.write: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        index = 0;
        for (int i = 0; i < reads; i++) {
            float f = list.get(index);
        }
        System.out.println("list.read: " + (System.currentTimeMillis() - start));
    }

    static void map() {
        Map<String, Float> map = new HashMap<>(size);

        for (int i = 0; i < size; i++)
            map.put("" + i, 0F);

        long start = System.currentTimeMillis();
        float value = 0.1F;
        int index = 0;
        for (int i = 0; i < writes; i++) {
            index++;
            map.put("" + index, value);
            if (index >= size) {
                index = 0;
                value += 0.1F;
            }
        }
        System.out.println("map.write: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        index = 0;
        for (int i = 0; i < reads; i++) {
            float f = map.get("" + index);
        }
        System.out.println("map.read: " + (System.currentTimeMillis() - start));
    }
}
