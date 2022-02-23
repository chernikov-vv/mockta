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

package codes.vps.mockta;

import javax.servlet.http.Cookie;

import org.apache.tomcat.util.http.SameSiteCookies;

import lombok.Getter;
import lombok.Setter;

public class OurCookie extends Cookie {
	/**
	 * Constructs a cookie with a specified name and value.
	 * <p>
	 * The name must conform to RFC 2109. That means it can contain only ASCII
	 * alphanumeric characters and cannot contain commas, semicolons, or white space
	 * or begin with a $ character. The cookie's name cannot be changed after
	 * creation.
	 * <p>
	 * The value can be anything the server chooses to send. Its value is probably
	 * of interest only to the server. The cookie's value can be changed after
	 * creation with the <code>setValue</code> method.
	 * <p>
	 * By default, cookies are created according to the Netscape cookie
	 * specification. The version can be changed with the <code>setVersion</code>
	 * method.
	 *
	 * @param name
	 *            a <code>String</code> specifying the name of the cookie
	 * @param value
	 *            a <code>String</code> specifying the value of the cookie
	 * @throws IllegalArgumentException
	 *             if the cookie name contains illegal characters (for example, a
	 *             comma, space, or semicolon) or it is one of the tokens reserved
	 *             for use by the cookie protocol
	 * @see #setValue
	 * @see #setVersion
	 */
	public OurCookie(String name, String value) {
		super(name, value);
	}

	@Getter
	@Setter
	private SameSiteCookies sameSite;

}
