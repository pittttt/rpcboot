package cn.pitt.test.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import cn.pitt.annotation.RpcService;
import cn.pitt.test.entity.User;
import cn.pitt.test.service.UserService;

/**
 * 用户服务实现类
 * 
 */
@Service
@RpcService(interfaces = { UserService.class })
public class UserServiceImpl implements UserService {

	@Override
	public User getUserById(Long id) {
		User user = new User();
		user.setId(id);
		user.setName("pitt");
		user.setSex(1);
		user.setCreateTime(new Date());

		return user;
	}

}
