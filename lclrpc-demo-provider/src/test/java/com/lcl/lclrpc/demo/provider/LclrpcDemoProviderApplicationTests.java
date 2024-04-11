package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.test.TestZKServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class LclrpcDemoProviderApplicationTests {

	static TestZKServer zkServer = new TestZKServer();

	@BeforeAll
	static void init() {
		zkServer.start();
	}

	@Test
	void contextLoads() {
		log.info(" ===> LclrpcDemoProviderApplicationTests  .... ");
	}

	@AfterAll
	static void destory() {
		zkServer.stop();
	}

}
