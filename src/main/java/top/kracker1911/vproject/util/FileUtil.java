package top.kracker1911.vproject.util;

import com.alibaba.fastjson.JSONReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static final String CLASS_FILE_PATH = FileUtil.class.getResource("/").getPath();
    public static final String RESOURCE_FILE_PATH = CLASS_FILE_PATH.replaceAll("classes/$", "resources/");

    public static String readJsonFile(String jsonFilePath){
        File jsonFile = new File(jsonFilePath);
        String result = null;
        InputStream is = null;
        InputStreamReader isr = null;
        JSONReader reader = null;
        try {
            is = new FileInputStream(jsonFile);
            isr = new InputStreamReader(is, "UTF-8");
            reader = new JSONReader(isr);
            result = reader.readString();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
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
