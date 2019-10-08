package com.flash.message.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flash.message.entity.HttpPckParams;
import com.flash.message.entity.HttpProducerEntity;
import com.flash.message.entity.HttpSubmitParams;
import com.flash.message.entity.HttpVarParams;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年9月5日 下午2:44:50
 *
 */
public class SwitchObj2Submit {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SwitchObj2Submit.class);

    public static List<HttpProducerEntity> obj2sub(Object obj, String modle) {
        if (obj == null) {
            return null;
        }
        List<HttpProducerEntity> submit = new ArrayList<HttpProducerEntity>();
        if (obj instanceof HttpSubmitParams) {
            HttpSubmitParams common = (HttpSubmitParams) obj;
            String[] mobiles = common.getMobile().split(",");
            for (int i = 0; i < mobiles.length; i++) {
            	//生成ID
            	String id = "";
				try {
					id = OrderIdGenerator.geneOrderIDBySnowFlake("http");
				} catch (Exception e) {
					LOGGER.error(e.getMessage(),e);
				}
                HttpProducerEntity tmp = new HttpProducerEntity();
                tmp.setAccount(common.getAccount());
                tmp.setPswd(common.getPswd());
                tmp.setMsg(common.getMsg());
                tmp.setExtno(common.getExtno());
                tmp.setNeedstatus(common.getNeedstatus());
                tmp.setMobile(mobiles[i]);
                tmp.setOwnMsgId(id);
                submit.add(tmp);
            }
        } else if (obj instanceof HttpVarParams) {
            HttpVarParams var = (HttpVarParams) obj;
            JSONArray array = JSONObject.parseArray(var.getMsg());
            for (int i = 0; i < array.size(); i++) {
                HttpProducerEntity tmp = new HttpProducerEntity();
              //生成ID
            	String id = "";
				try {
					id = OrderIdGenerator.geneOrderIDBySnowFlake("http");
				} catch (Exception e) {
					LOGGER.error(e.getMessage(),e);
				}
                tmp.setAccount(var.getAccount());
                tmp.setPswd(var.getPswd());
                JSONObject content = array.getJSONObject(i);
                tmp.setMobile(content.getString("mobile"));
                String msg = replaceVar(content, modle);
                tmp.setMsg(msg);
                tmp.setExtno(var.getExtno());
                tmp.setNeedstatus(var.getNeedstatus());
                tmp.setOwnMsgId(id);
                submit.add(tmp);
            }
        } else if (obj instanceof HttpPckParams) {
            HttpPckParams pck = (HttpPckParams) obj;
            JSONArray array = JSONObject.parseArray(pck.getMsg());
            for (int i = 0; i < array.size(); i++) {
            	//生成ID
            	String id = "";
				try {
					id = OrderIdGenerator.geneOrderIDBySnowFlake("http");
				} catch (Exception e) {
					LOGGER.error(e.getMessage(),e);
				}
                HttpProducerEntity tmp = new HttpProducerEntity();
                tmp.setAccount(pck.getAccount());
                tmp.setPswd(pck.getPswd());
                tmp.setNeedstatus(pck.getNeedstatus());
                tmp.setExtno(pck.getExtno());
                JSONObject content = array.getJSONObject(i);
                tmp.setMobile(content.getString("mobile"));
                tmp.setMsg(content.getString("content"));
                tmp.setOwnMsgId(id);
                submit.add(tmp);
            }
        }
        return submit;
    }

    private static String replaceVar(JSONObject content, String modle) {
        ExpressionParser ep = new SpelExpressionParser();
        EvaluationContext ctx = new StandardEvaluationContext();
        content.remove("mobile");
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            String value = (String) entry.getValue();
            String key = entry.getKey();
            ctx.setVariable(key, value);
        }
        return (String) ep.parseExpression(modle, new TemplateParserContext()).getValue(ctx);
    }

    public static void main(String[] args) {
        String modle = "告警发生时间 #{#alarmTime}，位置是在#{#location}";
        JSONObject content = new JSONObject();
        content.put("alarmTime", "2018-09-26 13:00:00");
        content.put("location", "二楼201机房");
        content.put("mobile", "19956596675");

        System.out.println(replaceVar(content, modle));
    }
}
