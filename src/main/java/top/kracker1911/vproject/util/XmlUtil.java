package top.kracker1911.vproject.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import top.kracker1911.vproject.exception.AesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/* xml工具 */
public class XmlUtil {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    /* 线程安全 */
    private static final XmlMapper mapper = new XmlMapper();

    /* 对象转化为xml字符串 */
    public static String toString(Object object){
        try{
            return mapper.writeValueAsString(object);
        }catch (JsonProcessingException ex){
            logger.error("XML转换错误", ex);
            return "";
        }
    }

    /* xml字符串转化为指定类对象 */
    public static <T> T fromString(String json,Class T){
        try{
            return (T)mapper.readValue(json, T);
        }catch (Exception ex){
            logger.error("XML解析错误", ex);
            return null;
        }
    }

    public static Map<String, Object> extractToMap(String xmltext) throws AesException {
        Map<String, Object> result;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xmltext);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);
            Element root = document.getDocumentElement();
            result = recursiveXml2Map(root);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ParseXmlError);
        }
    }

    private static Map<String, Object> recursiveXml2Map(Node parentNode) {
        Objects.requireNonNull(parentNode);
        Map<String, Object> result = new HashMap<>();
        NodeList nodeList = parentNode.getChildNodes();
        if(nodeList.getLength() > 0){
            Node node;
            for(int i = 0; i < nodeList.getLength(); ++i){
                node = nodeList.item(i);
                if (node.getChildNodes().getLength() > 1) {
                    result.put(node.getNodeName(), recursiveXml2Map(node));
                } else if(node.getChildNodes().getLength() == 1) {
                    result.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
        return result;
    }
}
