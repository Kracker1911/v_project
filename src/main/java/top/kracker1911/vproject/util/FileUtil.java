package top.kracker1911.vproject.util;

import com.alibaba.fastjson.JSONReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static final String CLASS_FILE_PATH = FileUtil.class.getResource("/").getPath();
    public static final String RESOURCE_FILE_PATH = CLASS_FILE_PATH.replaceAll("classes/$", "resources/");

    public static String[] readTextFile(String filePath) {
        Objects.requireNonNull(filePath);
        File textFile = new File(filePath);
        String[] result = new String[]{};
        if (textFile.exists() && textFile.isFile() && textFile.canRead()) {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                is = new FileInputStream(textFile);
                isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                br = new BufferedReader(isr);
                Stream<String> stream = br.lines();
                if (stream != null) {
                    return stream.toArray(String[]::new);
                } else {
                    return result;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (isr != null) {
                        isr.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } else {
            return result;
        }
        return result;
    }

    public static String readJsonFile(String jsonFilePath) {
        File jsonFile = new File(jsonFilePath);
        String result = null;
        InputStream is = null;
        InputStreamReader isr = null;
        JSONReader reader = null;
        try {
            is = new FileInputStream(jsonFile);
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new JSONReader(isr);
            result = reader.readString();
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            logger.error("json file read error", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return result;
    }
}
