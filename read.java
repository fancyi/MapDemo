package com.lucy.fcy.mylucy.util;

import com.google.gson.Gson;
import com.lucy.fcy.mylucy.model.ChatMessage;
import com.lucy.fcy.mylucy.model.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by fcy on 2015/5/18.
 */
public class HttpUtils {
    private static final String CON_URL = "http://www.tuling123.com/openapi/api";//Api 地址
    private static final String API_KEY = "756e42191aada1d0763514ca0c33662c";//API KEY
    /**
     * 发送一个消息，得到返回的消息 111
     * @param msg
     * @return
     */
    public static ChatMessage sendMessage(String msg){
        ChatMessage chatMessage = new ChatMessage();
        String jsonRes = null;
        try {
            jsonRes = doGet(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Result result = null;
        try{
            result = gson.fromJson(jsonRes, Result.class);//解析数据
            chatMessage.setMsg(result.getText());//获取解析数据里面的文字信息
            chatMessage.setUrl(result.getUrl());//获取解析后的url
        } catch (Exception e){
            chatMessage.setMsg("服务器繁忙，请稍候再试");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(ChatMessage.Type.INCOMING);//设置信息为接收
        return chatMessage;
    }

    public static String doGet(String msg) throws IOException {
        String result = "";
        String url = setParams(msg);
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        // 取得输入流，并读取
        InputStream is = connection.getInputStream();
        int len = -1;
        byte[] bytes = new byte[128];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((len = is.read(bytes))!=-1){
            baos.write(bytes,0,len);
        }
        result = new String(baos.toByteArray());//转换成字符串
        if (baos!=null){
            baos.close();
        }
        if (is!=null){
            is.close();
        }
        return result;
    }

    private static String setParams(String msg) {
        String getURL = "";
        try {
               getURL = CON_URL+"?key=" + API_KEY + "&info=" + URLEncoder.encode(msg, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getURL;
    }
	//1111
}
