package codes.vps.mockta.util;

import codes.vps.mockta.db.IDPDB;
import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.OktaUser;
import codes.vps.mockta.obj.okta.OpenIDMetaData;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

public class TokenUtil {

    public static String getAccessToken(OktaUser user, OpenIDMetaData metaData, String clientId) throws JoseException {

        JwtClaims claims = new JwtClaims();
        populateCommonClaims(claims, user.getId(), metaData.getIssuer(), clientId);
        claims.setClaim("idp", IDPDB.getIdp().getId());
        claims.setClaim("cid", clientId);
        claims.setClaim("uid", user.getId());
        claims.setClaim("status", user.getStatus());
        claims.setClaim("esAppData2", user.getExtProfileProperties().get("esAppData2"));
        claims.setClaim("email", user.getEmail());
        claims.setClaim("login", user.getExtProfileProperties().get("login"));

        JsonWebSignature jws = new JsonWebSignature();
        signature(jws, claims);

        return jws.getCompactSerialization();

    }

    public static String getIdToken(OktaUser user, OpenIDMetaData metaData, String clientId, OktaSession session, String nonce) throws JoseException {

        JwtClaims claims = new JwtClaims();
        populateCommonClaims(claims, user.getId(), metaData.getIssuer(), clientId);
        claims.setClaim("email", user.getUserName());
        claims.setClaim("ver", 1);
        if (nonce != null) {
            claims.setClaim("nonce", nonce);
        }
        claims.setClaim("email_verified", true);
        if (session != null) {
            claims.setClaim("auth_time", session.getCreated().getTime());
        }

        JsonWebSignature jws = new JsonWebSignature();
        signature(jws, claims);


        return jws.getCompactSerialization();

    }

    private static void populateCommonClaims(JwtClaims claims, String subject, String issuer, String clientId) {

        claims.setSubject(subject);
        claims.setIssuer(issuer);
        claims.setAudience(clientId);
        claims.setIssuedAtToNow();
        NumericDate expiration = NumericDate.now();
        expiration.addSeconds(3600);
        claims.setExpirationTime(expiration);
        claims.setGeneratedJwtId();
        claims.setStringListClaim("amr", "pwd");
        claims.setClaim("idp", IDPDB.getIdp().getId());

    }

    private static void signature(JsonWebSignature jws, JwtClaims claims) {

        JsonWebKey jwk = KeysDB.getKey();
        jws.setPayload(claims.toJson());
        jws.setKey(((RsaJsonWebKey)jwk).getRsaPrivateKey());
        jws.setKeyIdHeaderValue(jwk.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

    }

}
