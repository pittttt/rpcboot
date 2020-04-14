package cn.pitt.conf;

import cn.pitt.util.IpUtils;

/**
 * 常量
 * 
 */
public interface Consts {

	/** zk服务根节点 */
	String ZK_SERVICE_ROOT = "/ZrpcServeres";

	/** 本机IP */
	String LOCAL_IP = IpUtils.getLocalAddress();
}
