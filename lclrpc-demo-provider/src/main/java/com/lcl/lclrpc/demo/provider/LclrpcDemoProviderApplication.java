package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.annotation.EnableLclrpc;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@SpringBootApplication
@RestController
@EnableLclrpc
public class LclrpcDemoProviderApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(LclrpcDemoProviderApplication.class, args);
	}

//
//	@Autowired
//	UserService userService;
//	@RequestMapping("/timeoutPort")
//	public RpcResponse timeoutPort(@RequestParam("timeoutPort") String timeoutPort) {
//		RpcResponse response = new RpcResponse();
//		userService.setTimeoutPort(timeoutPort);
//		response.setStatus(true);
//		response.setData("success：" + timeoutPort);
//		return response;
//	}


	/**
	 * @return {@link ApplicationRunner}
	 */
//	@Bean
//	ApplicationRunner providerRun(){
//		return args -> {
//			RpcRequest request = new RpcRequest();
//			request.setService("com.lcl.lclrpc.demo.api.UserService");
//			request.setMethodSign("getUserById@1_java.lang.Integer");
//			request.setParameters(new Object[]{100});
//			RpcResponse rpcResponse = providerInvoker.invoke(request);
//		};
//	}
}

