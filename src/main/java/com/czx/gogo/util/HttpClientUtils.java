package com.czx.gogo.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {

    public static String post(String url,String json) throws InterruptedException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建HttpPost对象，设置url访问地址
        HttpPost httpPost = new HttpPost(url);

        //声明list集合，封装请求集合，封装表单中的参数
        StringEntity s = new StringEntity(json, "utf-8");

        List<NameValuePair> params = new ArrayList<NameValuePair>();


        //创建表单的Entity对象
        String charst="utf8";

        //设置表单Entity对象到POST请求中
        httpPost.setEntity(s);
        httpPost.setHeader("Content-Type","application/json");
//        httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");

        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");

        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");

        httpPost.setHeader("Accept", "application/json, text/plain, */*");

        httpPost.setHeader("apptype", "1");

        httpPost.setHeader("sec-ch-ua", "Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Google Chrome\";v=\"110");

        httpPost.setHeader("sec-ch-ua-mobile", "?0");

        httpPost.setHeader("sec-ch-ua-platform", "Windows");

        httpPost.setHeader("sec-fetch-dest", "empty");

        httpPost.setHeader("sec-fetch-mode", "cors");

        httpPost.setHeader("sec-fetch-site", "same-site");

        httpPost.setHeader("Connection", "keep-alive");

        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");

        //发起请求
//        System.out.println("发起请求的信息"+httpPost);

        //try/catch/finally : Ctrl+Alt+T
        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpPost);

            //解析响应
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("===========================================");
//                System.out.println(content);
                return content;
            }

        } catch (IOException e) {
            TimeUnit.SECONDS.sleep(1);
           return post(url, json);
        } finally {
            //关闭response
            try {
                if (response!=null)
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String post(String url, String json, Map<String,String> header) throws InterruptedException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建HttpPost对象，设置url访问地址
        HttpPost httpPost = new HttpPost(url);

        //声明list集合，封装请求集合，封装表单中的参数
        StringEntity s = new StringEntity(json, "utf-8");

        List<NameValuePair> params = new ArrayList<NameValuePair>();


        //创建表单的Entity对象
        String charst="utf8";

        //设置表单Entity对象到POST请求中
        httpPost.setEntity(s);
        httpPost.setHeader("Content-Type","application/json");
//        httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");

        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");

        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");

        httpPost.setHeader("Accept", "application/json, text/plain, */*");

        httpPost.setHeader("apptype", "1");

        httpPost.setHeader("sec-ch-ua", "Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Google Chrome\";v=\"110");

        httpPost.setHeader("sec-ch-ua-mobile", "?0");

        httpPost.setHeader("sec-ch-ua-platform", "Windows");

        httpPost.setHeader("sec-fetch-dest", "empty");

        httpPost.setHeader("sec-fetch-mode", "cors");

        httpPost.setHeader("sec-fetch-site", "same-site");

        httpPost.setHeader("Connection", "keep-alive");

        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");


        header.forEach(httpPost::setHeader);


        //发起请求
//        System.out.println("发起请求的信息"+httpPost);

        //try/catch/finally : Ctrl+Alt+T
        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpPost);

            //解析响应
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("===========================================");
//                System.out.println(content);
                return content;
            }

        } catch (IOException e) {
            TimeUnit.SECONDS.sleep(1);
            return post(url, json);
        } finally {
            //关闭response
            try {
                if (response!=null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String get(String key) {
        String result = "";
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("game", "csgo"));
            params.add(new BasicNameValuePair("text", "key"));

            URI uri = new URIBuilder().setScheme("http").setHost("buff.163.com")
                    .setPath("api/market/search/suggest")
                    .setParameters(params).build();
            HttpGet get = new HttpGet(uri);//这里发送get请求
            BasicClientCookie cookie = new BasicClientCookie("session", "1-Kh2ecZBgVtpcA8ZdvAboH4y5pcEo8juKerQ4X_vLJBgU2043160865");
            cookie.setDomain("buff.163.com");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);

            BasicClientCookie cookie2 = new BasicClientCookie("Device-Id", "jXnhV6XDbnB1rMQldmkP");
            cookie2.setDomain("buff.163.com");
            cookie2.setPath("/");
            cookieStore.addCookie(cookie2);

            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(get);
            // 判断网络连接状态码是否正常(0--200都数正常)
            //解析响应
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("===========================================");
//                System.out.println(content);
                return content;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public static String get(String key,List<BasicClientCookie> cookies,List<NameValuePair> params) {
        String result = "";
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            URI uri = new URIBuilder(key)
                    .setParameters(params).build();
            HttpGet get = new HttpGet(uri);//这里发送get请求

            for (int i = 0; i < cookies.size(); i++) {
                cookieStore.addCookie(cookies.get(i));
            }
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(get);
            // 判断网络连接状态码是否正常(0--200都数正常)
            //解析响应
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("===========================================");
//                System.out.println(content);
                return content;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public static String get(String key,List<NameValuePair> params) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            URI uri = new URIBuilder(key)
                    .setParameters(params).build();
            HttpGet get = new HttpGet(uri);//这里发送get请求

            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(get);
            // 判断网络连接状态码是否正常(0--200都数正常)
            //解析响应
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
//                System.out.println("===========================================");
//                System.out.println(content);
                return content;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
