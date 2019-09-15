package top.kracker1911.vproject.util;

import java.util.UUID;

public class UUIDUtil {

    public static String getUUID(){
         return UUID.randomUUID().toString();
    }


    public static String get32UUID(){
        return getUUID().trim().replaceAll("-", "");
    }
}
