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

import com.sun.istack.internal.NotNull;
import org.springframework.lang.Nullable;

import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public class Util {

    public static String randomId() {

        byte[] id = new byte[12];
        ThreadLocalRandom.current().nextBytes(id);
        return Base64.getEncoder().encodeToString(id);

    }

    /**
     * This method trims the specified string, and returns NULL if the string
     * was {@code null}, or empty.
     *
     * @param s string to trim
     * @return trimmed string, or {@code null}
     */
    @Nullable
    public static String sTrim(String s) {

        if (s == null) { return null; }
        StringBuilder sb = new StringBuilder();
        boolean trailing = false;
        int wsPos = -1;

        int l = s.length();
        for (int i=0; i<l; i++) {

            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {

                if (trailing && wsPos < 0) {
                    wsPos = i;
                }

            } else {
                if (wsPos >= 0) {
                    sb.append(s, wsPos, i);
                    wsPos = -1;
                }
                sb.append(c);
                trailing = true;
            }

        }

        if (sb.length() == 0) { return null; }
        return sb.toString();

    }

    @Nullable
    public static <S, T> S ifNotNull(@Nullable T val, @NotNull Function<T, S> fun, @Nullable Supplier<S> whenNull) {
        try {
            if (val == null) {
                if (whenNull == null) { return null; }
                return whenNull.get();
            }
            return fun.apply(val);
        } catch (Exception e) {
            throw doThrow(e);
        }
    }

    @NotNull
    public static <S, T> S makeNotNull(@Nullable T val, @NotNull Function<T, S> fun, @NotNull Supplier<S> whenNull) {
        S ret = ifNotNull(val, fun, whenNull);
        if (ret == null) { throw new NullPointerException("not null must be produced"); }
        return ret;
    }

    @NotNull
    public static <T> T makeNotNull(@Nullable T val, @NotNull Supplier<T> whenNull) {
        return makeNotNull(val, a->a, whenNull);
    }

    /**
     * Enables to throw an exception as a run-time exception.
     * This method does not declare any thrown exceptions, but any exception
     * can be safely passed to it, so it is re-thrown to the caller as-is.
     * @param e exception to throw
     * @return thrown exception.
     */
    // Thanks to http://blog.jooq.org/2012/09/14/throw-checked-exceptions-like-runtime-exceptions-in-java/
    public static RuntimeException doThrow(@NotNull Throwable e) {
        return doThrow0(e);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException doThrow0(@NotNull Throwable e) throws E {
        throw (E) e;
    }


}
