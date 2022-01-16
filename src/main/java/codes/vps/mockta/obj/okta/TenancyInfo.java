package codes.vps.mockta.obj.okta;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TenancyInfo extends RepresentationModel<TenancyInfo> {
	private final Tenancy tenancyInfo;

	@JsonCreator
	public TenancyInfo(@JsonProperty("tenancyinfo") Tenancy tenancyInfo) {
		super();
		this.tenancyInfo = tenancyInfo;
	}

	public Tenancy getTenancyInfo() {
		return tenancyInfo;
	}

}
