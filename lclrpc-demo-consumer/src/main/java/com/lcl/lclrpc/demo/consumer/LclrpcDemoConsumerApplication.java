package com.lcl.lclrpc.demo.consumer;
import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.consumer.ConsumerConfig;
import com.lcl.lclrpc.demo.api.Order;
import com.lcl.lclrpc.demo.api.OrderService;
import com.lcl.lclrpc.demo.api.User;
import com.lcl.lclrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@SpringBootApplication
@Import({ConsumerConfig.class})
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
	@Bean
	ApplicationRunner consumer_runner() {
		return args -> {
			User user = userService.getUserById(100);
			System.out.println(user);

			int id = userService.getId(100);
			System.out.println("========" + id);

			String string = userService.toString();
			System.out.println("========" + string);

			String name = userService.getName();
			System.out.println("========" + name);

			int height = userService.getHeight();
			System.out.println("========" + height);

//			Order order = orderService.getOrderById(404);
//			System.out.println(order);
		};
	}
}

