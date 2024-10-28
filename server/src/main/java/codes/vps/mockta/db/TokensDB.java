package codes.vps.mockta.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokensDB {

    private final static Map<String, Tokens> tokensByUser = new ConcurrentHashMap<>();

    public static void addTokens(String userId, String accessToken, String idToken) {

        Tokens tokens = new Tokens();
        tokens.accessToken = accessToken;
        tokens.idToken = idToken;

        tokensByUser.put(userId, tokens);

    }

    public static Tokens getTokens(String uid) {
        return tokensByUser.get(uid);
    }

    public static class Tokens {
        public String accessToken;
        public String idToken;
    }
}
