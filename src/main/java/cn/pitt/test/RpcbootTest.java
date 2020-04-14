package cn.pitt.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.pitt.test.controller.TestController;
import cn.pitt.test.entity.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RpcbootTest {

	@Autowired
	private TestController testController;

	@Test
	public void test() {
		User user = testController.getUserById(456L);
		System.out.println(user);
	}
}
