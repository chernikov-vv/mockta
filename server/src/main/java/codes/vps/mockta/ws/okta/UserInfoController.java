package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.*;
import codes.vps.mockta.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = {"/oauth2/v1/userinfo", "oauth2/{authServer}/v1/userinfo"})
public class UserInfoController {

    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request, @PathVariable(required = false) String authServer) {

        String header = request.getHeader("authorization");

        if (header == null || !header.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String token = header.substring(7);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setSkipDefaultAudienceValidation()
                .setVerificationKey(KeysDB.getKey().getKey()) // verify the signature with the public key
                .build();
        try {
            JwtClaims claims = jwtConsumer.processToClaims(token);
            String uid = (String) claims.getClaimValue("uid");

            // to support /revoke endpoint
            TokensDB.Tokens tokens = TokensDB.getTokens(uid);
            String accessToken = tokens.accessToken;
            if (!Objects.equals(token, accessToken)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            OktaUser user = UserDB.getUser(uid);

            return new ResponseEntity<>(new UserInfo(user.represent()), HttpStatus.OK);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot process token", ex);
        }

    }

}
