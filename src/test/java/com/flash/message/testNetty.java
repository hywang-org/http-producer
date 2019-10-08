package com.flash.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPoolConfig;

public class testNetty {
	
    @Test
    public void step01_sendGet() {
        String result = requestGet("http://127.0.0.1:8080");
        System.out.println("ResultInfo = " + result);
    }

    @Test
    public void step01_sendPost() {
        String result = requestPost("http://127.0.0.1:8080");
        System.out.println("ResultInfo = " + result);
    }

	@Test
	public void createRedisAppData() {
		Map<String, Object> appMap = new HashMap<String, Object>();
		appMap.put("max_connection", (int) 4);
		appMap.put("app_secret", "123456");

		StringRedisTemplate temple = new StringRedisTemplate();
		temple.setConnectionFactory(
				connectionFactory("192.168.0.104", 6379, "iflyrec!", 1000, 16, 0, 3000, false));
		temple.afterPropertiesSet();
		System.out.println(temple.opsForHash().get("109001", "total_num"));
		temple.execute(new SessionCallback<Object>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Object execute(RedisOperations operations) throws DataAccessException {
				operations.opsForValue().set("test", "1");
				return null;
			}
		});
//		temple.opsForHash().get("appId01", "speed_limit");
//		temple.opsForHash().putAll("109002", appMap);
	}

	public RedisConnectionFactory connectionFactory(String hostName, int port, String password, int maxIdle,
			int maxTotal, int index, long maxWaitMillis, boolean testOnBorrow) {
		JedisConnectionFactory jedis = new JedisConnectionFactory();
		jedis.setHostName(hostName);
		jedis.setPort(port);
		if (!StringUtils.isEmpty(password)) {
			jedis.setPassword(password);
		}
		if (index != 0) {
			jedis.setDatabase(index);
		}
		jedis.setPoolConfig(poolCofig(maxIdle, maxTotal, maxWaitMillis, testOnBorrow));
		jedis.afterPropertiesSet();
		// 初始化连接pool
		return jedis;
	}

	public JedisPoolConfig poolCofig(int maxIdle, int maxTotal, long maxWaitMillis, boolean testOnBorrow) {
		JedisPoolConfig poolCofig = new JedisPoolConfig();
		poolCofig.setMaxIdle(maxIdle);
		poolCofig.setMaxTotal(maxTotal);
		poolCofig.setMaxWaitMillis(maxWaitMillis);
		poolCofig.setTestOnBorrow(testOnBorrow);
		return poolCofig;
	}

    private String requestGet(String url) {
        String signature = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("signature", signature);
        System.out.println("url = " + url);
        System.out.println("signature = " + signature);
        CloseableHttpResponse response = null;
        String responseString = null;
        try {
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String message = "call servie failed: " + response.getStatusLine();
                System.out.println(message);
            }
            // HttpEntity entity = response.getEntity();
            // byte[] responseContent =
            // IOUtils.toByteArray(entity.getContent());
            // responseString = IOUtils.toString(responseContent, "utf-8");
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(response);
        }
        return responseString;

    }

    private String requestPost(String url) {
        String signature = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("signature", signature);
        System.out.println("url = " + url);
        System.out.println("signature = " + signature);
        // HttpEntity reqEntity =
        // EntityBuilder.create().setBinary(uploadContent)
        // .setContentType(ContentType.create("application/json",
        // "UTF-8")).build();
        // httppost.setEntity(reqEntity);
        CloseableHttpResponse response = null;
        String responseString = null;
        try {
            response = client.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String message = "call servie failed: " + response.getStatusLine();
                System.out.println(message);
            }
            HttpEntity entity = response.getEntity();
            byte[] responseContent = IOUtils.toByteArray(entity.getContent());
            responseString = IOUtils.toString(responseContent, "utf-8");
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(response);
        }
        return responseString;
    }

    private String requestPost2(String url, Map<String, Object> map, byte[] uploadContent) {
        String signature = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("signature", signature);
        System.out.println("url = " + url);
        System.out.println("signature = " + signature);
        HttpEntity reqEntity = EntityBuilder.create().setBinary(uploadContent)
                .setContentType(ContentType.create("application/json", "UTF-8")).build();
        httppost.setEntity(reqEntity);
        CloseableHttpResponse response = null;
        String responseString = null;
        try {
            response = client.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String message = "call servie failed: " + response.getStatusLine();
                System.out.println(message);
            }
            HttpEntity entity = response.getEntity();
            byte[] responseContent = IOUtils.toByteArray(entity.getContent());
            responseString = IOUtils.toString(responseContent, "utf-8");
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(response);
        }
        return responseString;
    }
}
