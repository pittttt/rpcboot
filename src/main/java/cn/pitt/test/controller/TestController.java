package cn.pitt.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cn.pitt.annotation.RpcReference;
import cn.pitt.test.entity.User;
import cn.pitt.test.service.UserService;

@RestController
public class TestController {

	@RpcReference
	private UserService userService;

	@GetMapping("/test/users/{id:[\\d]+}")
	public User getUserById(@PathVariable("id") Long userId) {
		return userService.getUserById(userId);
	}

}
