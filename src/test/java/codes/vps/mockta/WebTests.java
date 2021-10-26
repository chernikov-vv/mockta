package codes.vps.mockta;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

@SpringBootTest(classes = MocktaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, args = "--mockta.api-token=FireAxe")
public abstract class WebTests {

    @Autowired
    MocktaApplication app;

    @LocalServerPort
    protected int serverPort;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    protected Cookies cookies = new Cookies();

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
                .cookies(cookies)
                .port(serverPort)
                .log().ifValidationFails()
                .then()
                .log().ifValidationFails()
                .given();
    }

    protected <T> T request(Supplier<RequestSpecification> request, Function<RequestSpecification, ValidatableResponse> exec, Function<ValidatableResponse, T> extract) {

        ValidatableResponse vr = exec.apply(request.get());
        saveGlobalCookies(vr.extract().detailedCookies());
        if (extract == null) { return null; }
        return extract.apply(vr);

    }

    protected synchronized void saveGlobalCookies(Cookies newCookies) {

        // $TODO: Cookies are not identified by name, but are identified by path.
        // we are completely ignoring that, and that's wrong if we ever have cookies
        // that are set on different paths. We don't care about that in Mockta because
        // all the cookies are ever set to "/", but this may have to change.

        List<Cookie> newCookiesList = new ArrayList<>(newCookies.asList());

        // delete the existing cookies that are being set in the new list
        for (Iterator<Cookie> i = cookies.iterator(); i.hasNext(); ) {

            if (newCookiesList.isEmpty()) { break; }

            for (Iterator<Cookie> j = newCookiesList.iterator(); j.hasNext(); ) {

                Cookie oldC = i.next();
                Cookie newC = j.next();

                if (!Objects.equals(oldC.getName(), newC.getName())) {
                    continue;
                }

                System.out.println("Removing cookie "+newC.getName());

                i.remove();
                j.remove();
                break;

            }

        }

        // now add all the new cookies, except the ones with empty value, because those
        // are requests to delete them.

        List<Cookie> oldCookiesList = new ArrayList<>(cookies.asList());
        for (Cookie c : newCookies.asList()) {
            if (c.getValue() == null) { continue; }
            oldCookiesList.add(c);
            System.out.println("Adding cookie "+c.getName()+"->"+c.getValue());
        }
        cookies = new Cookies(oldCookiesList);

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

    protected <T> T userHtml(Function<RequestSpecification, ValidatableResponse> exec, Function<ValidatableResponse, T> post) {
        return request(this::userHtml, exec, post);
    }

    protected RequestSpecification user() {
        // we set the "Accept" header, otherwise we get application/hal+json
        return request().accept("application/json");
    }

    protected <T> T user(Function<RequestSpecification, ValidatableResponse> exec, Function<ValidatableResponse, T> post) {
        // we set the "Accept" header, otherwise we get application/hal+json
        return request(this::user, exec, post);
    }

    protected <T> T user(Function<RequestSpecification, ValidatableResponse> exec) {
        return user(exec, null);
    }

    RequestSpecification userJson() {
        return user().contentType("application/json");
    }

}
