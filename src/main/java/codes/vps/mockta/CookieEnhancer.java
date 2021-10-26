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

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

// https://linuxtut.com/en/0a2aca650721626055a3/
@Component
public class CookieEnhancer implements TomcatContextCustomizer {

    @Override
    public void customize(Context context) {

        context.setCookieProcessor(new Rfc6265CookieProcessor(){

            ThreadLocal<Cookie> cookie = new ThreadLocal<>();

            @Override
            public String generateHeader(Cookie cookie, HttpServletRequest request) {
                try {
                    this.cookie.set(cookie);
                    return super.generateHeader(cookie, request);
                } finally {
                    this.cookie.remove();
                }
            }

            @Override
            public SameSiteCookies getSameSiteCookies() {
                Cookie c = this.cookie.get();
                if (c instanceof OurCookie) {
                    return ((OurCookie) c).getSameSite();
                }
                return super.getSameSiteCookies();
            }
        });

    }

}
