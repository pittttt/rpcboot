package cn.pitt.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.pitt.annotation.RpcService;
import cn.pitt.conf.Consts;
import cn.pitt.util.RpcException;
import cn.pitt.zk.ZkServer;

/**
 * RPC实例注册服务
 * 
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	// rpc服务
	private Map<String, Object> handlers = new HashMap<String, Object>();

	// rpc服务IP
	private String serviceIp = Consts.LOCAL_IP;

	// rpc服务PORT
	private int servicePort;

	// zookeeper服务
	private ZkServer zkServer;

	public RpcServer(String serviceIp, int servicePort, ZkServer zkServer) {
		this.serviceIp = serviceIp;
		this.servicePort = servicePort;
		this.zkServer = zkServer;
	}

	/**
	 * 加载所有RPC服务 Spring容器会在加载完后调用
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> rpcServices = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (rpcServices != null && rpcServices.size() != 0) {
			for (Object rpcServiceBean : rpcServices.values()) {
				// 从业务实现类上的自定义注解中获取到value，然后获取到业务接口的全名
				Class<?>[] interfaces = rpcServiceBean.getClass().getAnnotation(RpcService.class).interfaces();
				for (Class<?> interfaceClass : interfaces) {
					String interfaceName = interfaceClass.getName();
					// 1、本地存根
					handlers.put(interfaceName, rpcServiceBean);
					// 2、注册到ZK
					registerToZk(interfaceName);
					LOGGER.info("服务实例{}注册成功！", rpcServiceBean.getClass().getName());
				}
			}
		}
	}

	/**
	 * 启动Netty服务 在setApplicationContext之后被Spring执行
	 */
	public void afterPropertiesSet() throws Exception {
		ServerListener.create(serviceIp, servicePort, handlers).start();
	}

	// 注册rpc对象信息到zk
	private void registerToZk(String interfaceName) {
		try {
			ZooKeeper zk = zkServer.getZk();
			// 1、创建服务接口节点,永久节点
			String interfaceNode = Consts.ZK_SERVICE_ROOT + "/" + interfaceName;
			Stat stat = zk.exists(interfaceNode, false);
			if (Objects.isNull(stat)) {
				zk.create(interfaceNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			// 2、创建服务对象节点，临时节点
			String beanNode = interfaceNode + "/" + serviceIp + ":" + servicePort + "?" + System.currentTimeMillis();
			zk.create(beanNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (Exception e) {
			throw new RpcException("注册服务到ZK异常！", e);
		}
	}

}