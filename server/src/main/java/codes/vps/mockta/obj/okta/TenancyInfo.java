package codes.vps.mockta.obj.okta;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TenancyInfo extends RepresentationModel<TenancyInfo> {
	private final List<Tenancy> tenancyInfo;

	@JsonCreator
	public TenancyInfo(@JsonProperty("tenancyinfo") List<Tenancy> tenancys) {
		super();
		this.tenancyInfo = tenancys;
	}

	public List<Tenancy> getTenancyInfo() {
		return tenancyInfo;
	}

}
