package top.kracker1911.vproject.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static List<String> toArray(String text) {
        return Arrays.asList(text.split(","));
    }

    public static String toString(List<String> array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (0 != i) {
                sb.append(",");
            }
            sb.append(array.get(i));
        }
        return sb.toString();
    }
}
