package cn.pitt.server;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.pitt.common.RpcRequest;
import cn.pitt.common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RPC请求服务处理器 读取请求，反射调用本地方法，返回结果
 * 
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Map<String, Object> handlerMap;

	public RpcServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	/**
	 * 接收消息，处理消息，返回结果
	 */
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {
		RpcResponse response = new RpcResponse();
		try {
			// 根据request来处理具体的业务调用
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable t) {
			response.setError(t);
		}
		// 写入 out，由RpcEncoder进行下一步编码处理，后发送到channel中给客户端
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 根据request来处理具体的业务调用 调用是通过反射的方式来完成
	 * 
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private Object handle(RpcRequest request) throws Throwable {
		// 查找服务类对象
		String interfaceName = request.getInterfaceName();
		Object serviceBean = handlerMap.get(interfaceName);

		// 拿到接口
		Class<?> forName = Class.forName(interfaceName);

		// 拿到要调用的方法名、参数类型、参数值
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Method method = forName.getMethod(methodName, parameterTypes);

		// 调用实现类对象的指定方法并返回结果
		Object[] parameters = request.getParameters();
		return method.invoke(serviceBean, parameters);
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOGGER.error("server caught exception", cause);
		ctx.close();
	}
}
