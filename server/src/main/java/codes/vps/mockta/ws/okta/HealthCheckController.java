package codes.vps.mockta.ws.okta;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import codes.vps.mockta.db.AppsDB;
import codes.vps.mockta.db.OktaApp;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<OktaApp>> getAllApplication() {
		return ResponseEntity.ok(AppsDB.getAllApps());
	}

}
