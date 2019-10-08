package com.flash.message;

import org.apache.http.impl.execchain.MainClientExec;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年9月3日 下午3:16:57
 *
 */
public class Test {

	public static JSONObject test() {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = 0; i < 10; i++) {
			JSONObject tmp = new JSONObject();
			tmp.put("mobile", "1995659667"+i);
			tmp.put("varName1"+i, "varValue1"+i);
			tmp.put("varName2"+i, "varValue2"+i);
			array.add(tmp);
		}
		json.put("msg", array);
		return json;
	}
	
	public static void main(String[] args) {
		System.out.println(test());
	}
}
