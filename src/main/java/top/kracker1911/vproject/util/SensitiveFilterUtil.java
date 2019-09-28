package top.kracker1911.vproject.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SensitiveFilterUtil {
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilterUtil.class);
    private static Set<String> sensitiveList;
    static{
        initSensitiveList();
    }

    private static void initSensitiveList(){
        if (sensitiveList == null){
            sensitiveList = new HashSet<>();
        }else {
            sensitiveList.clear();
        }
        String[] strList = FileUtil.readTextFile(FileUtil.RESOURCE_FILE_PATH + "sensitive-words.txt");
        sensitiveList = Arrays.stream(strList).collect(Collectors.toSet());
        logger.info("sensitive words list initialized");
    }

    private static String concatWords(String[] strings, String separator){
        Objects.requireNonNull(strings);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (0 != i){
                sb.append(separator);
            }
            sb.append(strings[i].replaceAll("[`~\\!@#\\$%\\^&\\*\\(\\)\\-\\_\\+=\\|/\\?<>,\\.;:'\"\\[\\]]", ""));
        }
        return sb.toString();
    }

    private static String generatePattern(String sw){
        String separators = "[`~\\!@#\\$%\\^&\\*\\(\\)\\-\\_\\+=\\|/\\?<>,\\.a-zA-Z0-9;:'\"\\[\\]]{0,}";
        return concatWords(sw.split(""), separators);
    }

    public static String filterMessage(String message){
        long start = System.currentTimeMillis();
        for(String sw : sensitiveList){
            String patternStr = generatePattern(sw);
            Matcher m = Pattern.compile(patternStr).matcher(message);
            if (m.find()){
                logger.info("filter message:" + message + ";sw:" + sw +";costs:" + (System.currentTimeMillis() - start) + "ms");
                return sw;
            }
        }
        logger.info("filter message:" + message + ";sw:;costs:" + (System.currentTimeMillis() - start) + "ms");
        return "";
    }
}
