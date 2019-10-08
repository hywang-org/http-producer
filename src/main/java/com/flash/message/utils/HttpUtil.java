package com.flash.message.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

/**
 * @author 作者 :hywang
 *
 * @version 创建时间：2019年8月31日 下午3:25:45
 *
 * @version 1.0
 */
public class HttpUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static JSONObject receivePost(HttpServletRequest request) {
        int length = request.getContentLength();
        byte[] jsonByte = new byte[length];
        try {
            request.getInputStream().read(jsonByte, 0, length);
        } catch (IOException e) {
            LOGGER.error("receivePost error", e);
        }
        JSONObject json = JSONObject.parseObject(jsonByte, JSONObject.class);
        return json;
    }

    /**
     * 获取body参数
     * 
     * @param request
     * @return
     */
    public static JSONObject getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        String content = buf.toString(CharsetUtil.UTF_8);
        JSONObject json = JSONObject.parseObject(content);
        return json;
    }
}
