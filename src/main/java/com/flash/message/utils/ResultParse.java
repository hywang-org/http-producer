package com.flash.message.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author 作者 :hywang
 *
 * @version 创建时间：2019年9月7日 下午4:01:07
 *
 * @version 1.0
 */
public class ResultParse {

    public static JSONObject parseSuc() {
        JSONObject json = new JSONObject();
        json.put("success", true);
        JSONObject tmp = new JSONObject();
        tmp.put("resptime", System.currentTimeMillis());
        json.put("msg", tmp);

        return json;
    }

    public static JSONObject parseErr(String code, String desc) {
        JSONObject json = new JSONObject();
        json.put("success", false);
        JSONObject tmp = new JSONObject();
        tmp.put("resptime", System.currentTimeMillis());
        tmp.put("code", code);
        tmp.put("desc", desc);
        json.put("msg", tmp);

        return json;
    }

}
