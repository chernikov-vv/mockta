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

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.OktaUser;
import codes.vps.mockta.db.UserDB;
import codes.vps.mockta.model.User;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.core.LinkBuilderSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController implements AdminService {

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try (OktaUser oktaUser = UserDB.addUser(user)) {
            return ResponseEntity.ok(oktaUser.represent());
        }
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        try (OktaUser oktaUser = UserDB.getUser(userId)) {
            return ResponseEntity.ok(oktaUser.represent());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String from, @RequestParam(required = false) Integer limit) {

        HttpHeaders responseHeaders = new HttpHeaders();
        if (limit == null || limit > 10) {
            limit = 10;
        }

        var page = UserDB.page(from, limit);
        boolean hasSelf = !page.data().isEmpty();
        if (from == null && hasSelf) {
            from = page.data().get(0).getId();
        }
        boolean hasNext = page.next() != null;

        if (hasSelf) {
            ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
            builder.replaceQueryParam("from", from);
            builder.replaceQueryParam("limit", limit);
            responseHeaders.add("link", new MyLinkBuilder(builder.build()).withSelfRel().toString());
        }

        if (hasNext) {
            ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
            builder.replaceQueryParam("from", page.next());
            builder.replaceQueryParam("limit", limit);
            responseHeaders.add("link", new MyLinkBuilder(builder.build()).withRel(IanaLinkRelations.NEXT).toString());
        }

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(page.data().stream().map(OktaUser::represent).toList());

    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllUsers() {

        boolean isRemoved = UserDB.deleteAllUser();
        if (isRemoved) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {

        boolean isRemoved = UserDB.deleteUser(userId);
        if (isRemoved) {
            return new ResponseEntity<>(userId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    static class MyLinkBuilder extends LinkBuilderSupport<MyLinkBuilder> {

        protected MyLinkBuilder(UriComponents c) {
            super(c);
        }

        @Override
        protected MyLinkBuilder getThis() {
            return this;
        }

        @Override
        protected MyLinkBuilder createNewInstance(UriComponents components, List<Affordance> affordances) {
            return new MyLinkBuilder(components);
        }
    }

}
