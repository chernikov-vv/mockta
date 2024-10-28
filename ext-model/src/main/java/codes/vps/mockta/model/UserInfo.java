package codes.vps.mockta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

/**
 * /userinfo endpoint user representation different with User. So needs a separate object for this endpoint
 */
@Getter
@Setter
public class UserInfo extends RepresentationModel<UserInfo> {

    // https://developer.okta.com/docs/reference/api/oidc/#userinfo
    private String sub;
    private String name;
    private String nickname;
    @JsonProperty("family_name")
    private String familyName;
    private String zoneinfo;
    private String locale;
    private String email;
    private String status;
    private String esAppData2;
    @JsonProperty("preferred_username")
    private String preferredUsername;

    public UserInfo(User user) {

        Profile p = user.getProfile();
        sub = user.getId();
        name = p.getFirstName();
        familyName = p.getLastName();
        zoneinfo = p.getTimeZone();
        locale = p.getLocale();
        email = p.getEmail();
        status = user.getStatus();
        esAppData2 = p.get("esAppData2");
        preferredUsername = p.getLogin();
    }
}
