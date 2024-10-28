package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.db.TokensDB;
import jakarta.servlet.http.HttpServletRequest;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/oauth2/v1/revoke", "oauth2/{authServer}/v1/revoke"})
public class TokenRevoke {

    @PostMapping
    public HttpEntity<?> post(HttpServletRequest request,
                              @RequestParam String token,
                              @RequestParam(required = false) String token_type_hint,
                              @PathVariable(required = false) String authServer) throws JoseException {

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                .setVerificationKey(KeysDB.getKey().getKey()) // verify the signature with the public key
                .build();
        try {
            JwtClaims claims = jwtConsumer.processToClaims(token);
            String uid = (String) claims.getClaimValue("uid");
            TokensDB.addTokens(uid, null, null);


        } catch (InvalidJwtException e) {
            throw new RuntimeException(e);
        }
        return HttpEntity.EMPTY;


    }
}
