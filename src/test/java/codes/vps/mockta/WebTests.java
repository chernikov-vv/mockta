package codes.vps.mockta;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@SpringBootTest(classes = MocktaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, args = "--mockta.api-token=FireAxe")
public abstract class WebTests {

    @Autowired
    MocktaApplication app;

    @LocalServerPort
    protected int serverPort;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    protected CookieFilter cookies = new CookieFilter();

    protected ObjectMapper jacksonObjectMapper;

    @BeforeEach
    protected void setUp() {
        jacksonObjectMapper = springMvcJacksonConverter.getObjectMapper();
    }

    protected String mapToJson(Object obj) {
        return Util.reThrow(()->jacksonObjectMapper.writeValueAsString(obj));
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, IOException {

        return jacksonObjectMapper.readValue(json, clazz);
    }

    protected RequestSpecification request() {
        return given()
                .when().redirects().follow(false)
                .filter(cookies)
                .port(serverPort)
                .log().ifValidationFails()
                .then()
                .log().ifValidationFails()
                .given();
    }

    RequestSpecification admin() {
        return request().header("Authorization", "SSWS "+app.getApiTokens().get(0));
    }

    RequestSpecification adminJson() {
        return admin().contentType("application/json");
    }

    protected RequestSpecification userHtml() {
        return user().accept("text/html");
    }

    protected RequestSpecification user() {
        // we set the "Accept" header, otherwise we get application/hal+json
        return request().accept("application/json");
    }

    RequestSpecification userJson() {
        return user().contentType("application/json");
    }

}
