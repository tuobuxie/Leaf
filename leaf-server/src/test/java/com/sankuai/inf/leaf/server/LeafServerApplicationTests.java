package com.sankuai.inf.leaf.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeafServerApplicationTests {

	@Test
	public void contextLoads() {
		RestTemplate restTemplate = new RestTemplate();
		String forObject = restTemplate.getForObject("http://172.20.8.5:8080/api/snowflakeIDC", String.class);
		System.out.println(forObject);
	}

}
