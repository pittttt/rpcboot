package cn.pitt.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IP相关工具类
 * 
 */
public class IpUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 获取本机IP
	 * 
	 * @return IP
	 */
	public static String getLocalAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error("获取本机IP异常！", e);
		}
		return "127.0.0.1";
	}
}
