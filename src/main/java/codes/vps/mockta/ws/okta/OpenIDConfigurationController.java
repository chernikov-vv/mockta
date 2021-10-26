/*
 * Copyright (c) 2021 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.obj.okta.OpenIDMetaData;
import org.jose4j.jwk.JsonWebKeySet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
public class OpenIDConfigurationController {

    @GetMapping(value={"/.well-known/openid-configuration", "oauth2/{authServer}/.well-known/openid-configuration"})
    public ResponseEntity<OpenIDMetaData> get(HttpServletRequest request, @PathVariable(required = false) String authServer) {
        return ResponseEntity.ok(new OpenIDMetaData(request, authServer));
    }

    @GetMapping("/oauth2/v1/keys")
    public ResponseEntity<JsonWebKeySet> get() {
        return ResponseEntity.ok(KeysDB.getKeys());
    }

}
