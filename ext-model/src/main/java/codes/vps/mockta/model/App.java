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

package codes.vps.mockta.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Map;

// https://developer.okta.com/docs/reference/api/apps/#application-object
@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@JsonDeserialize(builder = App.AppBuilder.class)
public class App extends RepresentationModel<App> {

    Date created;
    String id;
    String label;
    Date lastUpdated;
    String name;
    String profile;
    SignOnMode signOnMode;
    String status = "ACTIVE";
    AppSettings settings;
    Map<String, AppUser> users;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AppBuilder {}

}
