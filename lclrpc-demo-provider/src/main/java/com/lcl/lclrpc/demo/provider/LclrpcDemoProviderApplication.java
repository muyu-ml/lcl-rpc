package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.provider.ProviderConfig;
import com.lcl.lclrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class LclrpcDemoProviderApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(LclrpcDemoProviderApplication.class, args);
	}

	/**
	 *
	 */
	@Autowired
	private ProviderInvoker providerInvoker;

	/**
	 * 使用HTTP + JSON 作为序列化和通信协议
	 * @param request
	 * @return {@link RpcResponse}
	 */
	@RequestMapping("/")
	public RpcResponse invoke(@RequestBody RpcRequest request) {
		return providerInvoker.invoke(request);
	}


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
//			System.out.println("return : " + rpcResponse);
//		};
//	}
}

