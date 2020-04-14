package cn.pitt.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * rpc请求实体类
 * 
 */
@Getter
@Setter
@ToString
public class RpcRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 接口名称 */
	private String interfaceName;

	/** 方法名称 */
	private String methodName;

	/** 参数类型 */
	private Class<?>[] parameterTypes;

	/** 参数 */
	private Object[] parameters;

}
