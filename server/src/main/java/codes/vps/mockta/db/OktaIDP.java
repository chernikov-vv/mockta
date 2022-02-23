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

import codes.vps.mockta.Util;
import codes.vps.mockta.obj.okta.IDP;
import codes.vps.mockta.obj.okta.IDPType;
import lombok.Getter;

public class OktaIDP {

	@Getter
	private final String id = Util.randomId();

	public IDP represent() {
		return new IDP(id, IDPType.OKTA);
	}

}