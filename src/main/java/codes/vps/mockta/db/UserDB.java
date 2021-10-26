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

import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.obj.okta.User;
import lombok.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserDB {

    private final static Map<String, OktaUser> users = new ConcurrentHashMap<>();
    private final static Map<String, OktaUser> usersById = new ConcurrentHashMap<>();

    @NonNull
    public static OktaUser authenticate(String userName, String password) {

        OktaUser forUser = users.get(userName);
        if (forUser == null || !Objects.equals(password, forUser.getPassword())) {
            throw ErrorObject.authFailed("wrong password").boom();
        }
        return forUser;

    }

    public static OktaUser addUser(User user) {

        OktaUser oktaUser = new OktaUser(user);

        users.compute(oktaUser.getUserName(), (k,v)->{
            if (v != null) {
                // $TODO: I couldn't find anything in Okta documentation that says
                // what is to happen in case of an attempt to create a duplicate user.
                throw ErrorObject.duplicate("username "+k).boom();
            }
            return oktaUser;
        });

        usersById.put(oktaUser.getId(), oktaUser);
        return oktaUser;

    }

    @NonNull
    public static OktaUser getUser(String id) {

        OktaUser user = usersById.get(id);
        if (user == null) {
            throw ErrorObject.notFound("user id "+id).boom();
        }
        return user;

    }
}
