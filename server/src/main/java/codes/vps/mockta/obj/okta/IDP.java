/*
 * Copyright (c) 2021-2022 Pawel S. Veselov
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

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

// https://developer.okta.com/docs/reference/api/sessions/#idp-object
@Getter
public class IDP extends RepresentationModel<IDP> {

    private final String id;
    private final IDPType type;

    public IDP(String id, IDPType type) {
        this.id = id;
        this.type = type;
    }
}
