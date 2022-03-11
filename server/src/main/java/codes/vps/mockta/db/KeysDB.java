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

package codes.vps.mockta.db;

import codes.vps.mockta.util.Util;
import lombok.Getter;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;

public class KeysDB {

	// we only ever use one key at this point

	@Getter
	private final static JsonWebKeySet keys = new JsonWebKeySet();

	static {
		RsaJsonWebKey rsaJsonWebKey = Util.reThrow(()->RsaJwkGenerator.generateJwk(2048));
		rsaJsonWebKey.setKeyId(Util.randomId());
		keys.addJsonWebKey(rsaJsonWebKey);
	}

	public static JsonWebKey getKey() {
		return keys.getJsonWebKeys().get(0);
	}

}
