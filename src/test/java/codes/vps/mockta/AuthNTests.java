package codes.vps.mockta;

import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.AppUser;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.OAuthClient;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.PrimaryAuthentication;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AuthNTests extends WebTests {

    @SuppressWarnings("CollectionAddedToSelf")
    @Test
    public void successfulAuth() throws Exception {

        User user = new User(new Profile("test1@codes.vps", "Guy", "BlueShirt", null, null), new Credentials(new Password("BubbleGumIceCream")));
        GetNotNullString userId = new GetNotNullString();

        adminJson()
                .body(mapToJson(user))
                .post("/api/v1/users")
                .then()
                .statusCode(200)
                .body("id", userId)
                .body("profile.login", is(user.getProfile().getLogin()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName()))
                .body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"))
        ;

        App app = new App("test1.label", "test1", "test1",
                new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

        GetNotNullString appId = new GetNotNullString();

        adminJson().body(mapToJson(app)).post("/api/v1/apps").then()
                .statusCode(200)
                .body("id", appId)
                .body("label", is(app.getLabel()))
                .body("name", is(app.getName()))
                .body("settings.oauthClient.redirect_uris", is(app.getSettings().getOauthClient().getRedirectUris()));

        Map<String, String> profile = new LinkedHashMap<>();
        profile.put("foo", "bar");
        AppUser association = new AppUser(userId.getRecorded(), profile);

        adminJson()
                .body(mapToJson(association))
                .post("/api/v1/apps/{appId}/users", appId.getRecorded())
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("profile.foo", is(profile.get("foo")));

        for (int i=0; i<2; i++) {

            boolean useAuthServer = i == 1;

            user().get("/api/v1/sessions/me").then().statusCode(404);

            System.out.println("---> using auth server:"+useAuthServer);

            String baseUrl = "http://localhost:" + serverPort;

            GetSpecificString issuerId = new GetSpecificString(useAuthServer ? baseUrl  + "/oauth2/default" : baseUrl);
            GetNotNullString authUrl = new GetNotNullString();
            GetNotNullString logOutUrl = new GetNotNullString();

            String wkc = "/.well-known/openid-configuration";

            userJson()
                    .get(useAuthServer ? "oauth2/default" + wkc : wkc)
                    .then()
                    .statusCode(200)
                    .body("issuer", issuerId)
                    .body("authorization_endpoint", authUrl)
                    .body("end_session_endpoint", logOutUrl);

            // OK, we created all the admin objects, now we should try logging in.

            PrimaryAuthentication pa = new PrimaryAuthentication(null, null, null, "BubbleGumIceCream", null, "test1@codes.vps");

            GetNotNullString sessionToken = new GetNotNullString();

            userJson()
                    .body(mapToJson(pa))
                    .post("/api/v1/authn")
                    .then()
                    .statusCode(200)
                    .body("expiresAt", notNullValue())
                    .body("status", is("SUCCESS"))
                    .body("sessionToken", sessionToken);
            // $TODO: check HAL object(s)

            String nonce = Util.randomId();
            String state = Util.randomId();
            URI authUri = new DefaultUriBuilderFactory().uriString(authUrl.getRecorded())
                    .queryParam("client_id", appId.getRecorded())
                    .queryParam("nonce", nonce)
                    .queryParam("prompt", "none")
                    .queryParam("redirect_uri", "http://localhost/path")
                    .queryParam("response_mode", "okta_post_message")
                    .queryParam("response_type", "id_token")
                    .queryParam("sessionToken", sessionToken.getRecorded())
                    .queryParam("state", state)
                    .queryParam("scope", "openid email")
                    .build();

            // String content = saveCookies(userHtml().get(authUri).then().statusCode(200)).extract().body().asString();

            String content = userHtml().get(authUri).then().extract().body().asString();

            Document postDoc = Jsoup.parse(content);
            String javaScript = postDoc.selectFirst("script").data();

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

            Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            engineScope.put("window", engineScope);
            engineScope.put("self", engineScope);
            engineScope.put("parent", engineScope);
            engineScope.put("opener", engineScope);
            engineScope.put("top", engineScope);

            // http://mail.openjdk.java.net/pipermail/nashorn-dev/2013-December/002520.html
            // https://stackoverflow.com/a/33376041/622266
            engineScope.put("postMessage", new FakeWindow() {
                @Override
                public Object call(Object thiz, Object... args) {
                    Assertions.assertEquals("http://localhost", args[1]); // frameUrl
                    JSObject data = (JSObject) args[0];
                    Assertions.assertEquals(state, data.getMember("state"));
                    Assertions.assertFalse(data.hasMember("error"));
                    Assertions.assertFalse(data.hasMember("error_description"));

                    // $TODO: verify actual values inside the JWT
                    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                            .setExpectedIssuer(issuerId.getRecorded())
                            .setRequireExpirationTime() // the JWT must have an expiration time
                            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                            .setRequireSubject() // the JWT must have a subject claim
                            .setVerificationKey(KeysDB.getKey().getKey()) // verify the signature with the public key
                            .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                                    AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
                            .setExpectedAudience(appId.getRecorded())
                            .build(); // create the JwtConsumer instance
                    Assertions.assertDoesNotThrow(() -> jwtConsumer.processToClaims((String) data.getMember("id_token")));

                    return null;
                }

            });

            engine.eval(javaScript);

            ((Invocable) engine).invokeFunction("onWindowLoadHandler");

            for (int j=0; j<2; j++) {
                System.out.println("session try #"+j);
                user().get("/api/v1/sessions/me")
                        .then()
                        .statusCode(200)
                        .body("login", is("test1@codes.vps"));
            }

            String logOutState = Util.randomId();
            String rdr = new DefaultUriBuilderFactory().uriString("https://www.google.com?state=p&fix=in").build().toString();
            String fullLogOutUrl = new DefaultUriBuilderFactory().uriString(logOutUrl.getRecorded()).replaceQueryParam("state", logOutState).replaceQueryParam("post_logout_redirect_uri", rdr).build().toString();

            Matcher<Object> urlCheck = new BaseMatcher<Object>() {
                @Override
                public boolean matches(Object o) {
                    // https://stackoverflow.com/a/13592324/622266
                    UriComponents uc = UriComponentsBuilder.fromUriString((String)o).build();
                    if (!Objects.equals(uc.getScheme(), "https") ||
                            !Objects.equals(uc.getHost(), "www.google.com")) { return false; }
                    MultiValueMap<String, String> parameters = uc.getQueryParams();
                    return Arrays.equals(parameters.get("fix").toArray(), new Object[]{"in"}) &&
                            Arrays.equals(parameters.get("state").toArray(), new Object[]{logOutState});

                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("correct URL and state");
                }

            };

            user().when().get(fullLogOutUrl).then().statusCode(302).header("Location", urlCheck);

            user().get("/api/v1/sessions/me").then().statusCode(404);

        }

    }

    public static abstract class FakeWindow extends AbstractJSObject {

        @Override
        public boolean isFunction() {
            return true;
        }

    }

}
