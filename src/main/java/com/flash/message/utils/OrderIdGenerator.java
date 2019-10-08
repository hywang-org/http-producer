package com.flash.message.utils;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.downgoon.snowflake.util.BinHexUtil;

public class OrderIdGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderIdGenerator.class);
	
	private static final String DATA_TIME_FORMAT = "yyyyMMddHH";

	/**
	 * 序列号位数
	 */
	private static final long sequenceBits = 20L;
	/**
	 * 进程号位数
	 */
	private static final long progressIdBits = 16L;
	/**
	 * 随机值的位数
	 */
	private static final long randomBits = 6L;

	private static final long sequenceMask = ~(-1L << sequenceBits);

	private static final long randomShift = sequenceBits;
	private static final long progressShift = randomShift + randomBits;
	private static final long macsShift = progressShift + progressIdBits;

	private static final long mac;
	private static final long progressId;

	private static String lastDateStr = null;

	private static long sequence = 0L;

	private static final ByteBuffer buffer = ByteBuffer.allocate(4);

	public synchronized static String nextId() {
		String dateStr = getDateTimeStr();
		if (dateStr.equals(lastDateStr)) {
			sequence = sequence + 1 & sequenceMask;
		} else {
			sequence = 0L;
		}
		lastDateStr = dateStr;
		long suffix = (mac << macsShift) | (progressId << progressShift) | (getRandom() << randomShift) | sequence;
		return dateStr + BinHexUtil.hex(suffix).substring(3);
	}

	static Set<String> set = new HashSet<>(600000);

	private static String getDateTimeStr() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYMMddHHmm").withZone(ZoneId.systemDefault());
		String dateStr = formatter.format(Instant.now());
		return dateStr;
	}

	private static long getRandom() {
		return (long) floor(random() * 1024);
	}

	private static byte[] getMacAddress() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			byte[] mac;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				if (netInterface.isLoopback() || netInterface.isVirtual() || netInterface.isPointToPoint()
						|| !netInterface.isUp()) {
					continue;
				} else {
					mac = netInterface.getHardwareAddress();
					if (mac != null) {
						return mac;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("MAC地址获取失败", e);
		}
		return null;
	}

	/**
	 * 取mac地址的后10个字节
	 */
	private static int getLocalMac() {
		byte[] macAddress = getMacAddress();
		if (macAddress != null) {
			buffer.put(macAddress, 2, 4); // 取后四个字节
			buffer.flip();
			int tail4Bytes = buffer.getInt(); // mac地址的后四个字节
			int ip = Math.abs(tail4Bytes & 0b1111111111);
			LOGGER.info("订单号生成，mac: {}", ip);
			return ip;
		}
		return 0;
	}

	/**
	 * 当前进程的进程号
	 */
	private static int getProgressId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		LOGGER.info("订单号生成，进程号: {}", pid);
		return Integer.parseInt(pid);
	}

	static {
		mac = getLocalMac();
		progressId = getProgressId();
	}

	private static final FastDateFormat dateFormat = FastDateFormat.getInstance(DATA_TIME_FORMAT,
			TimeZone.getTimeZone("Etc/GMT-8"));

	public static String geneOrderIDBySnowFlake(String sysID) throws Exception {
		String times = dateFormat.format(new Date());
		return sysID + times + OrderIdGenerator.nextId();
	}

}
