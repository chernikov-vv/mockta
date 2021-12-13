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

package codes.vps.mockta.obj.okta;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

// https://developer.okta.com/docs/reference/api/users/#credentials-object
@Getter
public class Credentials extends RepresentationModel<Credentials> {

    private final Password password;
    private final RecoveryQuestion recoveryQuestion = null;
    private final Provider provider = new Provider();

    @JsonCreator
    public Credentials(Password password) {
        this.password = password;
    }

	public Password getPassword() {
		return password;
	}

	public RecoveryQuestion getRecoveryQuestion() {
		return recoveryQuestion;
	}

	public Provider getProvider() {
		return provider;
	}
    
}
