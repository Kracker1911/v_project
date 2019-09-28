package top.kracker1911.vproject.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StringUtil {

    public static List<String> toArray(String text) {
        return Arrays.asList(text.split(","));
    }

    public static String toString(List<String> array) {
        return concat(array.toArray(new String[]{}), ",");
    }

    public static String concat(String[] strings, String separator){
        Objects.requireNonNull(strings);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (0 != i){
                sb.append(separator);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }
}
