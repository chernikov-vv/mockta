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

package codes.vps.mockta.db;

import codes.vps.mockta.model.User;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.util.Page;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserDB {

    public final static Map<String, OktaUser> users = new ConcurrentHashMap<>();
    public final static LinkedHashMap<String, OktaUser> usersById = new LinkedHashMap<>();

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

        users.compute(oktaUser.getUserName(), (k, v) -> {
            if (v != null) {
                // $TODO: I couldn't find anything in Okta documentation that says
                // what is to happen in case of an attempt to create a duplicate user.
                throw ErrorObject.duplicate("username " + k).boom();
            }
            return new OktaUser(user);
        });

        users.put(oktaUser.getUserName(), oktaUser);
        usersById.put(oktaUser.getId(), oktaUser);

        return oktaUser;

    }

    @NonNull
    public static OktaUser getUser(String id) {

        OktaUser user = usersById.get(id);
        if (user == null) {
            throw ErrorObject.notFound("user id " + id).boom();
        }
        user.checkOut();
        return user;

    }

    public static boolean deleteUser(String id) {

        OktaUser user = usersById.get(id);
        if (user == null) {
            throw ErrorObject.notFound("user id " + id).boom();
        }
        usersById.remove(id);
        users.remove(user.getUserName());
        return true;

    }

    public static Page<OktaUser, String> page(String fromId, int pageSize) {

        boolean recording = false;
        String next = null;
        int recorded = 0;
        List<OktaUser> page = new ArrayList<>();

        for (Map.Entry<String, OktaUser> me : usersById.entrySet()) {

            String key = me.getKey();

            if (recorded == pageSize) {
                next = key;
                break;
            }

            if (!recording) {
                if (fromId == null || Objects.equals(fromId, key)) {
                    recording = true;
                } else {
                    continue;
                }
            }

            page.add(me.getValue());
            recorded++;

        }

        return new Page<>(usersById.size(), page, next);

    }

    public static boolean deleteAllUser() {

        usersById.clear();
        users.clear();

        return true;

    }

}
