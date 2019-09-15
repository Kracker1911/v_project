package top.kracker1911.vproject.util;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    public static final String UTF8_ENCODING = "UTF-8";
    private static final int TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 15000;

    /**
     * Execute the URL with the parameters over GET
     *
     * @param url
     * @param parameters
     * @return String
     * @throws Exception
     */
    public static String get(String url, Map<String, Object> parameters) throws Exception {
        String fullUrl = buildUrlByParameters(randmonURL(url), parameters);
        CloseableHttpClient httpclient = buildHttpClient(url);
        CloseableHttpResponse response = null;
        try {
            // 1.create get request
            HttpGet httpget = new HttpGet(fullUrl);
            httpget.addHeader(HTTP.CONTENT_TYPE, "application/octet-stream;charset=utf-8");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT).build();
            httpget.setConfig(requestConfig);
            // 2.execute get request
            response = httpclient.execute(httpget);
            // 3.get the content of response.
            HttpEntity entity = response.getEntity();
            // 4.status of response
            // logger.info(response.getStatusLine().toString());
            if (entity != null) {
                return EntityUtils.toString(entity, UTF8_ENCODING);
            }

        }catch(ConnectTimeoutException te){
            return "-100";
        }catch (Exception e) {
            logger.error("Failed to execute url=" + fullUrl + ", Error=" + e);
            throw new Exception(e);
        } finally {
            // 5.close connection
            closeConnection(response, httpclient);
        }
        return null;
    }

    /**
     * Execute the URL with the parameters over POST
     *
     * @param url
     * @param parameters
     * @return String
     */
    public static String postForm(String url, Map<String, Object> parameters) {
        CloseableHttpClient httpclient = buildHttpClient(url);
        HttpClientContext context = HttpClientContext.create();
        CloseableHttpResponse response = null;
        try {
            HttpPost httppost = new HttpPost(randmonURL(url));
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT).build();
            httppost.setConfig(requestConfig);

            // build the form by parameters
            httppost.setEntity(buildFormByParameters(parameters));
            response = httpclient.execute(httppost, context);
            if (response == null) {
                logger.error("Response is null !");
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String content = EntityUtils.toString(entity, UTF8_ENCODING);
                return content;
            }
        } catch (Exception e) {
            logger.error("Failed to execute url={}", url, e);
        } finally {
            // close connection
            closeConnection(response, httpclient);
        }
        return null;
    }

    /**
     * Common post for any action
     *
     * @param url
     * @param parameters
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, Object> parameters) throws Exception {
        CloseableHttpClient httpclient = buildHttpClient(url);
        CloseableHttpResponse response = null;
        try {
            // 1.create get request
            HttpPost httppost = new HttpPost(randmonURL(url));
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT).build();
            httppost.setConfig(requestConfig);
            // 2.execute post request
            StringEntity se = new StringEntity(JSON.toJSONString(parameters), UTF8_ENCODING);
            se.setContentType("text/html;charset=utf-8");
            httppost.setEntity(se);
            response = httpclient.execute(httppost);
            // 3.get the content of response.
            HttpEntity entity = response.getEntity();
            // 4.status of response
            // logger.debug(response.getStatusLine().toString());
            if (entity != null) {
                return EntityUtils.toString(entity, UTF8_ENCODING);
            }

        } catch (Exception e) {

            logger.error("Failed to execute url=" + url + ", Error=" + e);

            throw new Exception(e);
        } finally {
            // 5.close connection
            closeConnection(response, httpclient);
        }
        return null;
    }

    public static String post(String url, String json) {
        CloseableHttpClient httpclient = buildHttpClient(url);
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(randmonURL(url));
            //String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
            StringEntity se = new StringEntity(json,"utf-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            logger.error("Failed to execute url=" + url + ", Error=" + e);
        } finally {
            // close connection
            closeConnection(response, httpclient);
        }
        return null;
    }



    /**
     * Build the full URL by given URL and parameters
     *
     * @param url
     * @param parameters
     * @return String
     */
    private static String buildUrlByParameters(String url, Map<String, Object> parameters) {
        if (parameters != null) {
            StringBuilder sb = new StringBuilder(url + "?empty=");
            for (String key : parameters.keySet()) {
                sb.append("&").append(key).append("=").append(parameters.get(key));
            }
            return sb.toString();
        }
        return url;

    }

    /**
     * Build the form by given parameters
     *
     * @param parameters
     * @return UrlEncodedFormEntity
     * @throws UnsupportedEncodingException
     */
    private static UrlEncodedFormEntity buildFormByParameters(Map<String, Object> parameters)
            throws UnsupportedEncodingException {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                logger.info("Request parameter:<key=" + key + " :value=" + parameters.get(key) + ">");
                formparams.add(new BasicNameValuePair(key,
                        parameters.get(key) != null ? parameters.get(key).toString() : ""));
            }
        }
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, UTF8_ENCODING);
        return uefEntity;
    }

    /**
     * Close connection
     *
     * @param response
     * @param httpclient
     */
    private static void closeConnection(CloseableHttpResponse response, CloseableHttpClient httpclient) {
        try {
            if (response != null) {
                response.close();
            }
            if (httpclient != null) {
                httpclient.close();
            }
        } catch (Exception e) {
            logger.error("Failed to close connection", e);
        }
    }

    @SuppressWarnings("unused")
    private static String convertStreamToString(InputStream is) {
        BufferedReader br = null;
        StringBuffer sbf = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(is, UTF8_ENCODING));
            String line = null;
            while ((line = br.readLine()) != null) {
                sbf.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sbf.toString();
    }

    /**
     * Build http client which support https or not by URL
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient buildHttpClient(String url) {

        if (url.indexOf("https") == 0) {
            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                    .<ConnectionSocketFactory>create();
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                // 信任任何链接
                TrustStrategy anyTrustStrategy = new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        return true;
                    }
                };
                SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy)
                        .build();
                LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                registryBuilder.register("https", sslSF);
            } catch (KeyStoreException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
            return HttpClientBuilder.create().setConnectionManager(connManager).build();
        } else {
            return HttpClients.createDefault();
        }
    }

    /**
     *
     * @param urls
     * @return String
     */
    private static String randmonURL(String urls){
        String[] urlArray = urls.split(",");
        Random rand = new Random();
        int num = rand.nextInt(urlArray.length);
        return urlArray[num];
    }
}
