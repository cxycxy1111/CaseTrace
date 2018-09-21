package com.alfredteng.casetrace.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtil {

    public static final String TAG = "JSONHandler:";

    public JsonUtil() {
    }

    /**
     * String转为List<HashMap<String>>
     * @param resp 由响应体转化而来的字符串
     * @param keys
     * @return
     */
    public static ArrayList<Map<String,String>> strToListMap(String resp, String keys[]) throws IOException {
        ArrayList<Map<String,String>> list = new ArrayList<>();
        JSONArray ja = JSONArray.fromObject(resp);
        for(int i=0;i<ja.size();i++) {
            JSONObject jo = ja.getJSONObject(i);
            Map<String,String> map = new HashMap<>();
            map = jo;
            list.add(map);
        }
        return list;
    }

    /**
     * string转HashMap
     * @param str
     * @return
     * @throws IOException
     */
    public static HashMap<String,String> strToMap(String str) throws IOException {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        JSONObject jsonObject = JSONObject.fromObject(str);
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = jsonObject.getString(key);
            hashMap.put(key,value);
        }
        return hashMap;
    }

}
