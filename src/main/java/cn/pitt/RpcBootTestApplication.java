package cn.pitt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.pitt.annotation.EnableRpcConfiguration;

@SpringBootApplication
@EnableRpcConfiguration // 开启RPC服务
public class RpcBootTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpcBootTestApplication.class, args);
	}

}