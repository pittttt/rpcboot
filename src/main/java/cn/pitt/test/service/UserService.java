package cn.pitt.test.service;

import cn.pitt.test.entity.User;

/**
 * 用户服务
 * 
 */
public interface UserService {

	/**
	 * 根据id查询User
	 * 
	 * @param id
	 * @return
	 */
	User getUserById(Long id);

}
