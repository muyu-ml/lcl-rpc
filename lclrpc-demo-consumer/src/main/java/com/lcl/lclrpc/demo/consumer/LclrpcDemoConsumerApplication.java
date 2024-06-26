package com.lcl.lclrpc.demo.consumer;

import com.alibaba.fastjson.JSON;
import com.lcl.lclrpc.core.annotation.EnableLclrpc;
import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.api.RpcContext;
import com.lcl.lclrpc.core.cluster.GrayRouter;
import com.lcl.lclrpc.core.config.ConsumerConfigProperties;
import com.lcl.lclrpc.demo.api.User;
import com.lcl.lclrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@SpringBootApplication
@EnableLclrpc
@RestController
@Slf4j
public class LclrpcDemoConsumerApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(LclrpcDemoConsumerApplication.class, args);
	}

	@LclConsumer
	private UserService userService;

	@Autowired
	ConsumerConfigProperties consumerConfigProperties;


	/**
	 * @return {@link ApplicationRunner}
	 */

	@RequestMapping("/")
	public User getUserById(@RequestParam(name="id") int id) {
		User user = userService.findById(id);
		return user;
	}

	@RequestMapping("/find")
	public User find(@RequestParam("timeout") int timeout) {
		long start = System.currentTimeMillis();
		User user = userService.find(1000);
		log.info("userService.find  =======>>>>>  time: " + (System.currentTimeMillis() - start));
		return user;
	}


	@Autowired
	Router router;

	@RequestMapping("/gray")
	public String gray(@RequestParam(name="grayRatio") int grayRatio) {
		((GrayRouter)router).setGrayRatio(grayRatio);
		return "ok~ new grayRatio: " + grayRatio;
	}

	@RequestMapping("/consumerConfigProperties")
	public String getConsumerConfigProperties() {
		return JSON.toJSONString(consumerConfigProperties);
	}

	@Bean
	public ApplicationRunner consumer_runner() {
		return x -> {
//			 testAll();
		};
	}

	private void testAll() {
		// 常规int类型，返回User对象
		System.out.println("Case 1. >>===[常规int类型，返回User对象]===");
		User user = userService.findById(1);
		System.out.println("RPC result userService.findById(1) = " + user);

		// 测试方法重载，同名方法，参数不同
		System.out.println("Case 2. >>===[测试方法重载，同名方法，参数不同===");
		User user1 = userService.findById(1, "hubao");
		System.out.println("RPC result userService.findById(1, \"hubao\") = " + user1);

		// 测试返回字符串
		System.out.println("Case 3. >>===[测试返回字符串]===");
		System.out.println("userService.getName() = " + userService.getName());

		// 测试重载方法返回字符串
		System.out.println("Case 4. >>===[测试重载方法返回字符串]===");
		System.out.println("userService.getName(123) = " + userService.getName(123));

		// 测试local toString方法
		System.out.println("Case 5. >>===[测试local toString方法]===");
		System.out.println("userService.toString() = " + userService.toString());

		// 测试long类型
		System.out.println("Case 6. >>===[常规int类型，返回User对象]===");
		System.out.println("userService.getId(10) = " + userService.getId(10));

		// 测试long+float类型
		System.out.println("Case 7. >>===[测试long+float类型]===");
		System.out.println("userService.getId(10f) = " + userService.getId(10f));

		// 测试参数是User类型
		System.out.println("Case 8. >>===[测试参数是User类型]===");
		System.out.println("userService.getId(new User(100,\"Lcl\")) = " +
				userService.getId(new User(100,"Lcl")));


		System.out.println("Case 9. >>===[测试返回long[]]===");
		System.out.println(" ===> userService.getLongIds(): ");
		for (long id : userService.getLongIds()) {
			System.out.println(id);
		}

		System.out.println("Case 10. >>===[测试参数和返回值都是long[]]===");
		System.out.println(" ===> userService.getLongIds(): ");
		for (long id : userService.getIds(new int[]{4,5,6})) {
			System.out.println(id);
		}

		// 测试参数和返回值都是List类型
		System.out.println("Case 11. >>===[测试参数和返回值都是List类型]===");
		List<User> list = userService.getList(List.of(
				new User(100, "Lcl100"),
				new User(101, "Lcl101")));
		list.forEach(System.out::println);

		// 测试参数和返回值都是Map类型
		System.out.println("Case 12. >>===[测试参数和返回值都是Map类型]===");
		Map<String, User> map = new HashMap<>();
		map.put("A200", new User(200, "Lcl200"));
		map.put("A201", new User(201, "Lcl201"));
		userService.getMap(map).forEach(
				(k,v) -> System.out.println(k + " -> " + v)
		);

		System.out.println("Case 13. >>===[测试参数和返回值都是Boolean/boolean类型]===");
		System.out.println("userService.getFlag(false) = " + userService.getFlag(false));

		System.out.println("Case 14. >>===[测试参数和返回值都是User[]类型]===");
		User[] users = new User[]{
				new User(100, "Lcl100"),
				new User(101, "Lcl101")};
		Arrays.stream(userService.findUsers(users)).forEach(System.out::println);

		System.out.println("Case 15. >>===[测试参数为long，返回值是User类型]===");
		User userLong = userService.findById(10000L);
		System.out.println(userLong);

		System.out.println("Case 16. >>===[测试参数为boolean，返回值都是User类型]===");
		User user100 = userService.ex(false);
		System.out.println(user100);

		System.out.println("Case 17. >>===[测试服务端抛出一个RuntimeException异常]===");
		try {
			User userEx = userService.ex(true);
			System.out.println(userEx);
		} catch (RuntimeException e) {
			System.out.println(" ===> exception: " + e.getMessage());
		}

		System.out.println("Case 18. >>===[测试服务端抛出一个超时重试后成功的场景]===");
		// 超时设置的【漏斗原则】
		// A 2000 -> B 1500 -> C 1200 -> D 1000
		long start = System.currentTimeMillis();
		userService.find(1100);
		userService.find(1100);
		System.out.println("userService.find take "
				+ (System.currentTimeMillis()-start) + " ms");

		System.out.println("Case 19. >>===[测试通过Context跨消费者和提供者进行传参]===");
		String Key_Version = "rpc.version";
		String Key_Message = "rpc.message";
		RpcContext.setContextParameter(Key_Version, "v8");
		RpcContext.setContextParameter(Key_Message, "this is a v8 message");
		String version = userService.echoParameter(Key_Version);
		RpcContext.setContextParameter(Key_Version, "v9");
		RpcContext.setContextParameter(Key_Message, "this is a v9 message");
		String message = userService.echoParameter(Key_Message);
		System.out.println(" ===> echo parameter from c->p->c: " + Key_Version + " -> " + version);
		System.out.println(" ===> echo parameter from c->p->c: " + Key_Message + " -> " + message);
		// 放在 Filter 后置处理器中
//		RpcContext.ContextParameters.get().clear(); // 如果不清空，会导致线程复用时参数混乱
	}
}

