package com.flash.message.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flash.message.rabbitmq.service.RabbitmqService;

/**
* @author 作者 hywang 619201932@qq.com
*
* @version 创建时间：2019年9月2日 上午9:16:35
*
*/
@Controller
public class TestController {
	
	@Resource
	private RabbitmqService mqservice;

	@RequestMapping("/hello")
	@ResponseBody
	public String test() {
		String str = "123";
		try {
			mqservice.getConnection();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
}
