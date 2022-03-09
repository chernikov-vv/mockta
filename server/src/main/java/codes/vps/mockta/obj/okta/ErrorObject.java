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

import codes.vps.mockta.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

// https://developer.okta.com/docs/reference/error-codes/
@Getter
public class ErrorObject extends RepresentationModel<ErrorObject> {

    private final String errorCode;
    private final String errorSummary;
    private final String errorLink;
    private final String errorId;
    private final List<ErrorCause> errorCauses;
    @JsonIgnore
    private final HttpStatus httpError;

    // public final static ErrorObject AUTH_FAILED = new ErrorObject("E0000004", HttpStatus.UNAUTHORIZED, "Authentication failed");
    // public final static ErrorObject RESOURCE_NOT_FOUND = new ErrorObject("E0000007", HttpStatus.NOT_FOUND, "Resource not found");
    // public final static ErrorObject GENERIC_DUPLICATE = new ErrorObject("E0000108", HttpStatus.CONFLICT, "Object with the same primary reference already exists.");
    // public final static ErrorObject INVALID_SESSION = new ErrorObject("E0000005", HttpStatus.FORBIDDEN, "Invalid session");

    public static ErrorObject authFailed(String why) {
        return new ErrorObject("E0000004", HttpStatus.UNAUTHORIZED, String.format("Authentication failed:%s", why));
    }

    public static ErrorObject notFound(String what) {
        return new ErrorObject("E0000007", HttpStatus.NOT_FOUND, String.format("Resource not found:%s", what));
    }

    public static ErrorObject duplicate(String what) {
        return new ErrorObject("E0000108", HttpStatus.CONFLICT,
                String.format("Object with the same primary reference exists:%s", what));
    }

    public static ErrorObject invalidSession(String why) {
        return new ErrorObject("E0000005", HttpStatus.FORBIDDEN, String.format("Session invalid:%s", why));
    }

    public static ErrorObject illegalArgument(String what) {
        return new ErrorObject("E0000002", HttpStatus.BAD_REQUEST,
                String.format("The request was not valid: %s", what));
    }

    public ErrorObject(String errorCode, HttpStatus httpError, String errorSummary) {
        this(errorCode, httpError, errorSummary, errorCode, Collections.emptyList());
    }

    public ErrorObject(String errorCode, HttpStatus httpError, String errorSummary, String errorLink) {
        this(errorCode, httpError, errorSummary, errorLink, Collections.emptyList());
    }

    public ErrorObject(String errorCode, HttpStatus httpError, String errorSummary, String errorLink,
                       List<ErrorCause> errorCauses) {
        this.errorCode = errorCode;
        this.errorSummary = errorSummary;
        this.errorLink = errorLink;
        this.errorCauses = errorCauses;
        this.httpError = httpError;

        errorId = Util.randomId();

    }

    public MyException boom() {
        throw new MyException(this);
    }

    public static class MyException extends RuntimeException {

        @Getter
        private final ErrorObject object;

        public MyException(ErrorObject object) {
            this.object = object;
        }

        public MyException(ErrorObject object, String msg) {
            super(msg);
            this.object = object;
        }

        public MyException(ErrorObject object, Throwable cause) {
            super(cause);
            this.object = object;
        }
    }

}
