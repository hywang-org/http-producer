package com.flash.message.netty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flash.message.config.MsgDao;
import com.flash.message.config.Repository;
import com.flash.message.entity.HttpPckParams;
import com.flash.message.entity.HttpProducerEntity;
import com.flash.message.entity.HttpSubmitParams;
import com.flash.message.entity.HttpVarParams;
import com.flash.message.entity.order.ProOrder;
import com.flash.message.rabbitmq.service.RabbitmqService;
import com.flash.message.tabooword.core.TabooWordChecker;
import com.flash.message.utils.BeanValidator;
import com.flash.message.utils.Constant;
import com.flash.message.utils.DateUtil;
import com.flash.message.utils.HttpUtil;
import com.flash.message.utils.RedisConsts;
import com.flash.message.utils.RedisOperationSets;
import com.flash.message.utils.ResultParse;
import com.flash.message.utils.StateCode;
import com.flash.message.utils.SwitchObj2Submit;
import com.flash.message.utils.es.ResultEsDao;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @author 作者 hywang 619201932@qq.com
 *
 * @version 创建时间：2019年7月2日 下午2:12:42
 *
 */
public class HttpMessageServerHandler extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpMessageServerHandler.class);

	Map<String, Object> sourceMap;

	public HttpMessageServerHandler(Map<String, Object> sourceMap) {
		this.sourceMap = sourceMap;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest request = (FullHttpRequest) msg;
		try {
			// 检查和校验
			checkAndPck(ctx, msg);
		} catch (Exception e) {
			LOGGER.error("处理请求失败!", e.getMessage(), e);
		} finally {
			// 释放请求
			request.release();
		}
	}

	/**
	 * 发送的返回值
	 * 
	 * @param ctx     返回
	 * @param context 消息
	 * @param status  状态
	 */
	private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/*
	 * 建立连接时，返回消息
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
		ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
		super.channelActive(ctx);
	}

	private void checkAndPck(ChannelHandlerContext ctx, Object msg) throws IOException, TimeoutException {
		// 用来判断路径是否正确
		boolean flag = false;
		if (!(msg instanceof FullHttpRequest)) {
			JSONObject json = ResultParse.parseErr(HttpResponseStatus.BAD_REQUEST.toString(), "未知请求!");
			send(ctx, json.toJSONString(), HttpResponseStatus.BAD_REQUEST);
			return;
		}
		FullHttpRequest request = (FullHttpRequest) msg;
		String path = request.uri(); // 获取路径
		HttpMethod method = request.method();// 获取请求方法
		// 如果不是这个路径，就直接返回错误
		if (!path.startsWith(Constant.DEFAULT_URL)) {
			JSONObject json = ResultParse.parseErr(HttpResponseStatus.BAD_REQUEST.toString(), "未知请求!");
			send(ctx, json.toJSONString(), HttpResponseStatus.BAD_REQUEST);
			return;
		}

		if (!HttpMethod.POST.equals(method)) {
			JSONObject json = ResultParse.parseErr(HttpResponseStatus.METHOD_NOT_ALLOWED.toString(), "未知请求!");
			send(ctx, json.toJSONString(), HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		JSONObject body = HttpUtil.getBody(request);
		RedisOperationSets appRedis =  (RedisOperationSets) sourceMap.get("appRedis");
		RabbitmqService mqService = (RabbitmqService) sourceMap.get("mqservice");
		MsgDao ypDao = (MsgDao) sourceMap.get("ypDao");
		// 鉴权
		String appSecret = (String) appRedis.hGetValue(body.getString("account"), RedisConsts.APP_SECRET);
		if (StringUtils.isEmpty(appSecret)) {
			JSONObject json = ResultParse.parseErr(StateCode.PWD_ERROR.getCode(), "密码不正确");
			send(ctx, json.toJSONString(), HttpResponseStatus.FORBIDDEN);
			return;
		} else {
			// 密码不正确
			if (!appSecret.equals(body.getString("pswd"))) {
				JSONObject json = ResultParse.parseErr(StateCode.PWD_ERROR.getCode(), "密码不正确");
				send(ctx, json.toJSONString(), HttpResponseStatus.FORBIDDEN);
				return;
			}
		}
		// 封装请求
		List<HttpProducerEntity> submit = new ArrayList<HttpProducerEntity>();
		switch (path) {
		case Constant.SENDHTTPMSG_URL:
			HttpSubmitParams httpmsg = JSONObject.toJavaObject(body, HttpSubmitParams.class);
			// 参数校验
			BeanValidator.check(httpmsg);
			// 封装请求
			submit = SwitchObj2Submit.obj2sub(httpmsg, null);
			LOGGER.info("封装成功");
			break;
		case Constant.SENDPCKMSG_URL:
			HttpPckParams pck = JSONObject.toJavaObject(body, HttpPckParams.class);
			// 参数校验
			BeanValidator.check(pck);
			// 封装请求
			submit = SwitchObj2Submit.obj2sub(pck, null);
			break;
		case Constant.SENDVARMSG_URL:
			HttpVarParams var = JSONObject.toJavaObject(body, HttpVarParams.class);
			// 参数校验
			BeanValidator.check(var);
			// 封装请求
			JSONObject moduleJson = (JSONObject) appRedis.hGetValue(var.getAccount(), RedisConsts.MODULE_ID);
			String modle = moduleJson.getString(var.getAccount());
			submit = SwitchObj2Submit.obj2sub(var, modle);
			break;
		case Constant.QUERYBALANCE_URL:
			LOGGER.info("查询订单信息");
			break;
		default:
			LOGGER.error("非法请求");
			flag = true;
			break;
		}
		// 非定义类型，返回请求错误
		if (flag) {
			JSONObject json = ResultParse.parseErr(HttpResponseStatus.BAD_REQUEST.toString(), "未知请求!");
			send(ctx, json.toJSONString(), HttpResponseStatus.BAD_REQUEST);
			return;
		}
		String appId = body.getString("account");
		JSONArray ids = new JSONArray();
		for (int i = 0; i < submit.size(); i++) {
			HttpProducerEntity common = submit.get(i);
			// 禁忌词判断
			if (TabooWordChecker.containTabooWord(common.getMsg())) {
				JSONObject json = ResultParse.parseErr(StateCode.ILLEGAL_CONTENT.getCode(), "短信中含有敏感词汇，禁止发送！");
				send(ctx, json.toJSONString(), HttpResponseStatus.FORBIDDEN);
				return;
			}
			LOGGER.info("禁忌此判断");
			BigDecimal size = new BigDecimal(common.getMsg().length());
			BigDecimal num = size.divide(new BigDecimal(70), 0, BigDecimal.ROUND_UP);
			/*
			 * String deduction = loadFileContent("lua/deduction.lua"); // 计费逻辑 if
			 * (appRedis.eval(deduction, Collections.singletonList(appId), num.intValue()))
			 * { JSONObject json = ResultParse.parseErr(StateCode.FEE_NOT_ENOUGN.getCode(),
			 * "费用不足!"); send(ctx, json.toJSONString(), HttpResponseStatus.FORBIDDEN);
			 * return; }
			 */
			LOGGER.info("发布消息");
			mqService.publishMq(body.getString("account"), common);
			ids.add(common.getOwnMsgId());
			// 记录发送
			ProOrder order = new ProOrder();
			order.setOwnMsgId(common.getOwnMsgId());
			order.setAppId(body.getString("account"));
			order.setDesId(common.getMobile());
			order.setProtocol("http");
			order.setServerId(Repository.getStringProperty("serverId"));
			order.setShareDate(DateUtil.LocalDateToUdate());
			ypDao.save(order);

			// 短信内容保存到es
//            ResultEsDao esDao = (ResultEsDao) sourceMap.get("esDao");
//            esDao.upsertRersult(common);
		}
		JSONObject success = ResultParse.parseSuc();
		success.getJSONObject("msg").put("msgids", ids);
		send(ctx, success.toJSONString(), HttpResponseStatus.OK);
	}

	private String loadFileContent(String resourcePath) {
		try {
			File file = new ClassPathResource(resourcePath).getFile();
			Long fileLength = file.length();
			byte[] b = new byte[fileLength.intValue()];
			try (FileInputStream in = new FileInputStream(file)) {
				int length = in.read(b, 0, b.length);
				if (length == b.length || length == -1) { // 已经读满或者已经到了流的结尾
					return new String(b, StandardCharsets.UTF_8);
				}
				int temp;
				while ((temp = in.read(b, length, b.length - length)) != -1) {
					length += temp;
					if (length == b.length) {
						return new String(b, StandardCharsets.UTF_8);
					}
				}
				return new String(b, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			return null;
		}
	}
	
}
