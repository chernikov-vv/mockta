/*
 * Copyright (c) 2021 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package codes.vps.mockta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;

import codes.vps.mockta.ws.okta.AuthInterceptor;
import codes.vps.mockta.ws.okta.NoCacheInterceptor;
import lombok.Getter;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSpringHttpSession
@EnableSwagger2
public class MocktaApplication implements ApplicationRunner, WebMvcConfigurer {

	private final String API_KEY_OPT = "mockta.api-token";
	@Getter
	private List<String> apiTokens;

	private AuthInterceptor authInterceptor;
	private NoCacheInterceptor noCacheInterceptor;

	@Bean
	// $TODO: we need to set up session expiration
	public MapSessionRepository sessionRepository() {
		return new MapSessionRepository(new ConcurrentHashMap<>());
	}

	public static void main(String[] args) {


		SpringApplication.run(MocktaApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		System.out.println(
				"Application started with command-line arguments: {} " + Arrays.toString(args.getSourceArgs()));
		System.out.println("NonOptionArgs: {}" + args.getNonOptionArgs());
		System.out.println("OptionNames: {}" + args.getOptionNames());

		for (String name : args.getOptionNames()) {
			System.out.println("arg-" + name + "=" + args.getOptionValues(name));
		}

		List<String> apiTokens = args.getOptionValues(API_KEY_OPT);

		if (apiTokens != null) {
			this.apiTokens = Collections.unmodifiableList(new ArrayList<>(apiTokens));
		} else {
			this.apiTokens = Collections.emptyList();
		}
		 

	}

	@Autowired
	public void setNoCacheInterceptor(NoCacheInterceptor noCacheInterceptor) {
		this.noCacheInterceptor = noCacheInterceptor;
	}

	@Autowired
	public void setAuthInterceptor(AuthInterceptor authInterceptor) {
		this.authInterceptor = authInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor).excludePathPatterns("/api/v1/auth","/.well-known/openid-configuration","/oauth2/v1/keys","/oauth2/v1/authorize");
		registry.addInterceptor(noCacheInterceptor);
	}

	// https://www.baeldung.com/spring-boot-customize-jackson-objectmapper
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
				.serializers(new JsonWebKeysSerializer());
	}

	@Bean
	public Docket swaggerSettings() {
		Parameter parameter = new ParameterBuilder().name("Authorization").description("Authorization Token")
				.modelRef(new ModelRef("string")).parameterType("header").required(false).build();
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(parameter);
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().apiInfo(apiInfo()).pathMapping("/")
				.globalOperationParameters(parameters);
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("Mock Okta ", "Mock Okta  API", "API TOS", "Terms of service", "Arvind kapse",
				"License of API", "");
		return apiInfo;
	}

	@Bean
	public LinkDiscoverers discoverers() {
		List<LinkDiscoverer> plugins = new ArrayList<>();
		plugins.add(new CollectionJsonLinkDiscoverer());
		return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new EnumConverter());
	}

	public List<String> getApiTokens() {
		return apiTokens;
	}

	public void setApiTokens(List<String> apiTokens) {
		this.apiTokens = apiTokens;
	}

	public AuthInterceptor getAuthInterceptor() {
		return authInterceptor;
	}

	public NoCacheInterceptor getNoCacheInterceptor() {
		return noCacheInterceptor;
	}

}
