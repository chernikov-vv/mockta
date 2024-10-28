package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.db.TokensDB;
import jakarta.servlet.http.HttpServletRequest;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = {"/oauth2/v1/introspect", "oauth2/{authServer}/v1/introspect"})
public class TokenIntrospectController {

    @PostMapping
    public HttpEntity<?> post(HttpServletRequest request, @RequestParam String token, @RequestParam(required = false) String token_hint,
                              @PathVariable(required = false) String authServer) throws JoseException {

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                .setVerificationKey(KeysDB.getKey().getKey())
                .build();

        Map<String, Object> parameters = new HashMap<>();


        try {
            JwtClaims claims = jwtConsumer.processToClaims(token);
            String uid = (String) claims.getClaimValue("uid");

            // to support /revoke endpoint
            TokensDB.Tokens tokens = TokensDB.getTokens(uid);
            String accessToken = tokens.accessToken;
            if (Objects.equals(token, accessToken)) {
                parameters.put("active", true);
            } else {
                parameters.put("active", false);
            }
        } catch (Exception ex) {
            parameters.put("active", false);
        }

        return new ResponseEntity<>(parameters, HttpStatus.OK);

    }

}
