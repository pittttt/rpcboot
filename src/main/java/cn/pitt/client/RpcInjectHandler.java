package cn.pitt.client;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.pitt.annotation.RpcReference;
import cn.pitt.common.RpcRequest;
import cn.pitt.common.RpcResponse;
import cn.pitt.conf.Consts;
import cn.pitt.util.HashMultimap;
import cn.pitt.util.RpcException;
import cn.pitt.zk.ZkClient;

/**
 * RPC服务注册处理器
 * 
 * 为spring bean带RpcReference注解的属性注入代理rpc实例
 */
public class RpcInjectHandler implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// zookeeper中注册服务器
	private ZkClient zkClient;

	public RpcInjectHandler(ZkClient zkClient) {
		this.zkClient = zkClient;
	}

	// 所有服务实例
	private HashMultimap<String, String> servicesMap = HashMultimap.create();

	/**
	 * 对Spring Bean中带RpcReference注解的属性赋予代理对象
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			Object bean = applicationContext.getBean(beanName);
			Class<?> beanClass = bean.getClass();
			Arrays.stream(beanClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(RpcReference.class))
					.forEach(field -> {
						field.setAccessible(true);
						try {
							// 1、获取字段类型，只处理接口
							Class<?> fieldTypeClass = field.getType();
							if (fieldTypeClass.isInterface()) {
								// 1、从ZK订阅服务
								subscribeService(fieldTypeClass.getName());

								// 2、赋值代理对象
								field.set(bean, getProxy(fieldTypeClass));
							}
						} catch (KeeperException | InterruptedException e) {
							LOGGER.error("属性{}订阅服务异常！", field.getName(), e);
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							LOGGER.error("属性{}设值异常！", field.getName(), e);
							throw new RuntimeException(e);
						}
					});
		}

	}

	/**
	 * 从zk订阅指定服务
	 * 
	 * @param interfaceName
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void subscribeService(String interfaceName) throws KeeperException, InterruptedException {
		ZooKeeper zk = zkClient.getZk();
		String path = Consts.ZK_SERVICE_ROOT + "/" + interfaceName;
		Watcher watcher = new NodeChildrenChangedWatcher(servicesMap, zk);
		Stat stat = zk.exists(path, watcher);
		if (Objects.nonNull(stat)) {
			List<String> beanInfos = zk.getChildren(path, watcher);
			servicesMap.put(interfaceName, new HashSet<>(beanInfos));
		}
	}

	/**
	 * 获取JDK代理对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getProxy(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// 封装参数
				RpcRequest request = new RpcRequest();
				request.setMethodName(method.getName());
				request.setParameterTypes(method.getParameterTypes());
				request.setInterfaceName(clazz.getName());
				request.setParameters(args);

				// 获取随机服务
				String[] sv = getRandomServiceAddress(request.getInterfaceName()).split(":");
				RpcClientHandler client = new RpcClientHandler(sv[0], Integer.parseInt(sv[1]));

				// 发起远程调用
				RpcResponse response = client.send(request);
				if (response.getError() != null) {
					throw response.getError();
				}
				return response.getResult();
			}
		});
	}

	/**
	 * 获取一个随机的服务提供地址
	 * 
	 * @param interfaceName
	 * @return
	 */
	private String getRandomServiceAddress(String interfaceName) {
		Set<String> servicesInfo = servicesMap.get(interfaceName);
		Optional<String> optional = servicesInfo.stream().findAny();
		if (optional.isPresent()) {
			String serviceInfo = optional.get();
			return serviceInfo.substring(0, serviceInfo.indexOf("?"));
		}
		throw new RpcException("没有找到" + interfaceName + "的对应服务！");
	}

}
