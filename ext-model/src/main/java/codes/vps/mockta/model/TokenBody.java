package codes.vps.mockta.model;

public class TokenBody {

    String type;
    String accessToken;
    String idToken;

    public TokenBody(String type, String accessToken, String idToken) {
        this.type = type;
        this.accessToken = accessToken;
        this.idToken = idToken;
    }

}
