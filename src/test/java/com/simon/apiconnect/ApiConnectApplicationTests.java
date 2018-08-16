package com.simon.apiconnect;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiConnectApplicationTests {

	@Test
	public void contextLoads() {
		ApiConnectApplication.main(Arrays.array());
	}

}
