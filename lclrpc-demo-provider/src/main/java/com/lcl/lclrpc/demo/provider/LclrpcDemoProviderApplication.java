package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.annotation.EnableLclrpc;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.provider.ProviderConfig;
import com.lcl.lclrpc.core.provider.ProviderInvoker;
import com.lcl.lclrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

