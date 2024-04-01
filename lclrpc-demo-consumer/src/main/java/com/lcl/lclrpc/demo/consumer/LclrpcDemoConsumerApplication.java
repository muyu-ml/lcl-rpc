package com.lcl.lclrpc.demo.consumer;
import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.consumer.ConsumerConfig;
import com.lcl.lclrpc.demo.api.Order;
import com.lcl.lclrpc.demo.api.OrderService;
import com.lcl.lclrpc.demo.api.User;
import com.lcl.lclrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@SpringBootApplication
@Import({ConsumerConfig.class})
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

	@LclConsumer
	private OrderService orderService;

	/**
	 * @return {@link ApplicationRunner}
	 */

	@RequestMapping("/")
	public User getUserById(@RequestParam(name="id") int id) {
		User user = userService.getUserById(id);
		return user;
	}

//	@Bean
	ApplicationRunner consumer_runner() {
		return args -> {
			User user = userService.getUserById(100);
			log.info("" + user);

			User user1 = userService.getUserById(100, "tom");
			log.info("" + user1);

			int id = userService.getId(100);
			log.info("========" + id);

			String string = userService.toString();
			log.info("========" + string);

			String name = userService.getName();
			log.info("========" + name);

			long height = userService.getHeight();
			log.info("========" + height);

			long height1 = userService.getHeight(new User(100, "lcl"));
			log.info("========" + height1);

			int[] ids = userService.getIds();
			for(int i : ids) {
				log.info("========" + i);
			}

			long[] longids = userService.getLongIds();
			for(long i : longids) {
				log.info("========" + i);
			}

			int[] ids2 = userService.getIds(new int[] {2,4,6});
			for(int i : ids2) {
				log.info("========" + i);
			}




//			Order order = orderService.getOrderById(404);
//			log.info(order);
		};
	}
}

