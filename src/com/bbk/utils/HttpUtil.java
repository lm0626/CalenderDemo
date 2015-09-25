package com.bbk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.accounts.NetworkErrorException;

public class HttpUtil {
    /**
     * android访问网络有两种方法：
     * 1.HttpUrlConnection (jdk)
     * 2.HttpClient (apache-->android)
     * 每种方法都分为get/post两种方式
     * @throws Exception 
     */
    public static String doGetByHttpUrlConnection(String url,HashMap<String, String> params) throws Exception{
        url = toStringParams(url, params,true);
        System.out.println("url:"+url);
        //1.得到连接
        URL path = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) path.openConnection();
        //设置请求方式
        conn.setRequestMethod("GET");
        //设置请求超时时间
        conn.setConnectTimeout(5000);
        //判断是否连接成功---判断响应码是否是200
        int responseCode = conn.getResponseCode();
        if(responseCode!=200){
            throw new NetworkErrorException();
        }
        //得到服务器返回的输入流
        InputStream is = conn.getInputStream();
        String result = parseStreamToString(is);
        return result;
    }

    private static String parseStreamToString(InputStream is)
            throws IOException {
        //读取流-->String  (如果服务器返回的是一个文件，is-->file)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len =0;
        byte[] buffer = new byte[1024];
        while((len = is.read(buffer))!=-1){
            baos.write(buffer, 0, len);
        }
        String result = baos.toString();
        return result;
    }

    private static String toStringParams(String url,
        HashMap<String, String> params,boolean isGet) {//alt+shift+m
        //get方式要将参数拼接到url里面
        StringBuffer sb = new StringBuffer();
        if(params!=null){
            if(isGet){
                sb.append(url);
                sb.append("?");
            }
            for(Map.Entry<String, String> entry: params.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append("=").append(value).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            url = sb.toString();
        }
        System.out.println(url);
        return url;
    }
    
    /*
     * post方式
     */
    public static String doPostByHttpUrlConnection(String url,HashMap<String, String> params) throws Exception{
        //post传的参数：username=jack&pwd=123
        String postData = toStringParams(url, params, false);
        //1.得到连接
        URL path = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) path.openConnection();
        //设置请求方式
        conn.setRequestMethod("POST");
        //设置请求超时时间
        conn.setConnectTimeout(5000);
        //设置允许输出
        conn.setDoOutput(true);
        //2.传数据给服务器
        //得到输出流
        OutputStream os = conn.getOutputStream();
        os.write(postData.getBytes());
        /*
         *  java.net.ProtocolException: cannot write request body after response has been read
         */
        //判断是否连接成功---判断响应码是否是200
        int responseCode = conn.getResponseCode();
        if(responseCode!=200){
            throw new NetworkErrorException();
        }
        //3.得到服务器返回的数据
        InputStream is = conn.getInputStream();
        String result = parseStreamToString(is);
        return result;
    }

    /**
     * 使用HttpClient 
     * Get方式
     */
    public static String doGetByHttpClient(String url,HashMap<String, String> params) throws Exception{
        //拼接参数
        url = toStringParams(url, params, true);
        HttpClient client = new DefaultHttpClient();
        //设置超时时间
        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        //创建一个Get请求
        HttpGet request = new HttpGet(url);
        //执行请求--得到响应
        HttpResponse response = client.execute(request);
        //处理响应
        //先去判断下连接情况--响应码
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode!=200){
            throw new NetworkErrorException();
        }
        //得到返回结果
        Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            System.out.println(header);
        }
        //返回的结果实体
        HttpEntity entity = response.getEntity();
//      entity.getContentEncoding();//得到编码
//      entity.getContentLength();//得到流的长度
//      entity.getContentType();//得到内容的类型 比如 text/html
        //解析流
//      InputStream is = entity.getContent();
//      String result = parseStreamToString(is);
        //解析流2
        String result = EntityUtils.toString(entity, "utf-8");
        return result;
    }
    
    public static String doPostByHttpClient(String url,HashMap<String, String> params) throws Exception{
        HttpClient client = new DefaultHttpClient();
        //设置超时时间
        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        for(Map.Entry<String, String> entry: params.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            BasicNameValuePair pair = new BasicNameValuePair(key, value);
            parameters.add(pair);
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        if(response.getStatusLine().getStatusCode()!=200){
            throw new NetworkErrorException();
        }
        HttpEntity entity2 = response.getEntity();
        String result = EntityUtils.toString(entity2,"utf-8");
        return result;
    }
    

}
