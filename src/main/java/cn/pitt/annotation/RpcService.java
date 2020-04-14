package cn.pitt.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 将此类声明为RPC服务对象
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RpcService {

	// 支持多个接口
	Class<?>[] interfaces();
}
