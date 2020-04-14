package cn.pitt.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 为该属性注入RPC服务对象
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface RpcReference {

}
