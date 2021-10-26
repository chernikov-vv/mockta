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

import codes.vps.mockta.ws.okta.AuthInterceptor;
import codes.vps.mockta.ws.okta.NoCacheInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableSpringHttpSession
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
        registry.addInterceptor(authInterceptor);
        registry.addInterceptor(noCacheInterceptor);
    }

    // https://www.baeldung.com/spring-boot-customize-jackson-objectmapper
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .serializers(new JsonWebKeysSerializer());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConverter());
    }
}
