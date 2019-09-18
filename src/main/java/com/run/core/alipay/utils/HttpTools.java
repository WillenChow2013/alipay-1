package com.run.core.alipay.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpTools {


    private final static int CONNECT_TIME_OUT_MS = 60*1000;

    private final static int READ_TIME_OUT_MS = 80*1000;

    /**
     * <p>
     *     http POST请求
     * </p>
     * @param urlSuffix
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String require(String urlSuffix, String data, int connectTimeoutMs, int readTimeoutMs) throws ClientProtocolException, IOException {

        BasicHttpClientConnectionManager connManager;

        connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

        HttpPost httpPost = new HttpPost(urlSuffix);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();

        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        postEntity.setContentEncoding("UTF-8");
        httpPost.addHeader("Content-Type", "text/html;charset=UTF-8");
        httpPost.setEntity(postEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");

    }

    /**
     * <p>
     *     post请求
     * </p>
     * @param urlSuffix url
     * @param data 请求报文数据
     * @return
     */
    public static String post(String urlSuffix, String data) throws Exception {
        try {
            return require(urlSuffix,data,CONNECT_TIME_OUT_MS,READ_TIME_OUT_MS);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

}
