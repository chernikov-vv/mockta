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

package codes.vps.mockta.obj.model;

import codes.vps.mockta.util.Util;
import lombok.Getter;

@Getter
public class AuthInfo {

    private final String idToken;
    private final String state;
    private final String error;
    private final String errorDescription;
    private final String frameUrl;

    private AuthInfo(String idToken, String state, String error, String errorDescription, String frameUrl) {
        this.idToken = nes(idToken);
        this.state = nes(state);
        this.error = nes(error);
        this.errorDescription = nes(errorDescription);
        this.frameUrl = nes(frameUrl);
    }

    private String nes(String s) {
        if (s == null) {
            return null;
        }
        return Util.escapeForSqJsString(s);
    }

    public static AuthInfo onError(String frameUrl, String state, String error, String errorDescription) {
        return new AuthInfo(null, state, error, errorDescription, frameUrl);
    }

    public static AuthInfo onAuth(String frameUrl, String state, String idToken) {
        return new AuthInfo(idToken, state, null, null, frameUrl);
    }

}
