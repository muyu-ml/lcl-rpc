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

			User user1 = userService.getUserById(100, "tom");
			System.out.println(user1);

			int id = userService.getId(100);
			System.out.println("========" + id);

			String string = userService.toString();
			System.out.println("========" + string);

			String name = userService.getName();
			System.out.println("========" + name);

			long height = userService.getHeight();
			System.out.println("========" + height);

			long height1 = userService.getHeight(new User(100, "lcl"));
			System.out.println("========" + height1);

			int[] ids = userService.getIds();
			for(int i : ids) {
				System.out.println("========" + i);
			}

			long[] longids = userService.getLongIds();
			for(long i : longids) {
				System.out.println("========" + i);
			}

			int[] ids2 = userService.getIds(new int[] {2,4,6});
			for(int i : ids2) {
				System.out.println("========" + i);
			}




//			Order order = orderService.getOrderById(404);
//			System.out.println(order);
		};
	}
}

