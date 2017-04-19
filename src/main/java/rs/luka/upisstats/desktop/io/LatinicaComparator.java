package rs.luka.upisstats.desktop.io;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luka on 3.2.17..
 */
public class LatinicaComparator implements Comparator<String> {

    private static Map<Character, Integer> comparing = new HashMap<>();

    static {
        comparing.put('a', 64);
        comparing.put('b', 65);
        comparing.put('c', 66);
        comparing.put('č', 67);
        comparing.put('ć', 68);
        comparing.put('d', 69);
        comparing.put('đ', 70);
        comparing.put('e', 71);
        comparing.put('f', 72);
        comparing.put('g', 73);
        comparing.put('h', 74);
        comparing.put('i', 75);
        comparing.put('j', 76);
        comparing.put('k', 77);
        comparing.put('l', 78);
        comparing.put('m', 79);
        comparing.put('n', 80);
        comparing.put('o', 81);
        comparing.put('p', 82);
        comparing.put('r', 83);
        comparing.put('s', 84);
        comparing.put('š', 85);
        comparing.put('t', 86);
        comparing.put('u', 87);
        comparing.put('v', 88);
        comparing.put('z', 89);
        comparing.put('ž', 90);
    }

    @Override
    public int compare(String o1, String o2) {
        int len1 = o1.length();
        int len2 = o2.length();
        int lim = Math.min(len1, len2);
        char v1[] = o1.toLowerCase().toCharArray();
        char v2[] = o2.toLowerCase().toCharArray();

        int k = 0;
        while (k < lim) {
            char c1 = comparing.containsKey(v1[k]) ? (char)((int)comparing.get(v1[k])) : v1[k];
            char c2 = comparing.containsKey(v2[k]) ? (char)((int)comparing.get(v2[k])) : v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
}
