package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.*;

import codes.vps.mockta.obj.okta.OpenIDMetaData;
import codes.vps.mockta.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping(value = {"/oauth2/v1/token", "oauth2/{authServer}/v1/token"})
public class TokensController {

    Logger logger = LoggerFactory.getLogger(TokensController.class);

    // https://developer.okta.com/docs/reference/api/oidc/#token
    @PostMapping
    public HttpEntity<?> post(HttpServletRequest request, @RequestParam(name = "grant_type") String grantType,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String password,
                              @RequestParam String client_id,
                              @PathVariable(required = false) String authServer) throws JoseException {


        if (grantType.equals("password")) {
            return handlePasswordGrant(request, username, password, client_id, authServer);
        }
        return ResponseEntity.badRequest().body("Unsupported grant type");
    }


    private ResponseEntity<?> handlePasswordGrant(HttpServletRequest request, String username, String password, String clientId, String authServer) {

        try (OktaUser user = UserDB.authenticate(username, password)) {

            OpenIDMetaData metaData = new OpenIDMetaData(request, authServer);

            String idToken = TokenUtil.getIdToken(user, metaData, clientId, null, null);
            String accessToken = TokenUtil.getAccessToken(user, metaData, clientId);


            TokensDB.addTokens(user.getId(), accessToken, idToken);

            return ResponseEntity.ok(Collections.singletonMap("access_token", accessToken));

        } catch (Exception e) {
            logger.debug("Cannot retrieve user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }

    }

}
