package codes.vps.mockta.obj.okta;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tenancy extends RepresentationModel<Tenancy> {
	@JsonProperty("tenancy")
	private final String tenancy;
	@JsonProperty("roles")
	private final List<String> roles;

	@JsonCreator
	public Tenancy(@JsonProperty("tenancy") String tenancy, @JsonProperty("roles") List<String> roles) {
		super();
		this.tenancy = tenancy;
		this.roles = roles;
	}

	public String getTenancy() {
		return tenancy;
	}

	public List<String> getRoles() {
		return roles;
	}

}
