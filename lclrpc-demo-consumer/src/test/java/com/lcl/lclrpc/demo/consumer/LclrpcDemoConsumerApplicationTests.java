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

@SpringBootTest(classes = {LclrpcDemoConsumerApplication.class})
@Slf4j
class LclrpcDemoConsumerApplicationTests {


	static ApplicationContext context1;
	static ApplicationContext context2;

	static TestZKServer zkServer = new TestZKServer();

	@BeforeAll
	static void init() {
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		System.out.println(" =============     ZK2182    ========== ");
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		zkServer.start();
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		System.out.println(" =============      P8094    ========== ");
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		context1 = SpringApplication.run(LclrpcDemoProviderApplication.class,
				"--server.port=8094",
				"--lclrpc.zk.servers=localhost:2182",
				"--lclrpc.app.env=test",
				"--logging.level.com.lcl.lclrpc=info",
				"--lclrpc.provider.metas.dc=bj",
				"--lclrpc.provider.metas.gray=false",
				"--lclrpc.provider.metas.unit=B001"
		);
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		System.out.println(" =============      P8095    ========== ");
		System.out.println(" ====================================== ");
		System.out.println(" ====================================== ");
		context2 = SpringApplication.run(LclrpcDemoProviderApplication.class,
				"--server.port=8095",
				"--lclrpc.zk.servers=localhost:2182",
				"--lclrpc.app.env=test",
				"--logging.level.com.lcl.lclrpc=info",
				"--lclrpc.provider.metas.dc=bj",
				"--lclrpc.provider.metas.gray=false",
				"--lclrpc.provider.metas.unit=B002"
		);
	}

	@Test
	void contextLoads() {
		System.out.println(" ===> aaaa  .... ");
	}

	@AfterAll
	static void destory() {
		SpringApplication.exit(context1, () -> 1);
		SpringApplication.exit(context2, () -> 1);
		zkServer.stop();
	}

}
