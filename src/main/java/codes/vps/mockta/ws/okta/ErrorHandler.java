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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codes.vps.mockta.obj.okta.ErrorObject;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(ErrorObject.MyException.class)
	public ResponseEntity<ErrorObject> oktaErrorHandler(ErrorObject.MyException e) {
		return new ResponseEntity<>(e.getObject(), e.getObject().getHttpError());
	}

}
