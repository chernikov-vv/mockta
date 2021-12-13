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

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import codes.vps.mockta.db.OktaUser;
import codes.vps.mockta.db.UserDB;
import codes.vps.mockta.obj.okta.User;
import lombok.NonNull;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController implements AdminService {

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return ResponseEntity.ok(UserDB.addUser(user).represent());

    }
    
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(UserDB.getUser(userId).represent());
    }
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<OktaUser>> getAllUsers() {
        return ResponseEntity.ok(UserDB.getAllUsers());
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity deleteUser(@PathVariable String userId) {
    	
    	boolean isRemoved = UserDB.deleteUser(userId);
    	if(isRemoved) {
    		 return new ResponseEntity<>(userId, HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    }
    
   

}
