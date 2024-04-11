package com.lcl.lclrpc.demo.consumer;

import com.lcl.lclrpc.core.test.TestZKServer;
import com.lcl.lclrpc.demo.provider.LclrpcDemoProviderApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@Slf4j
class LclrpcDemoConsumerApplicationTests {


	static ApplicationContext context;

	static TestZKServer zkServer = new TestZKServer();

	@BeforeAll
	static void init() {
		log.info(" ====================================== ");
		zkServer.start();
		context = SpringApplication.run(LclrpcDemoProviderApplication.class,
				"--server.port=8084", "--lclrpc.zkServer=localhost:2182", "--logging.level.com.lcl.lclrpc=debug");
	}

	@Test
	void contextLoads() {
		System.out.println(" ===> aaaa  .... ");
	}

	@AfterAll
	static void destory() {
		SpringApplication.exit(context, () -> 1);
		zkServer.stop();
	}

}
