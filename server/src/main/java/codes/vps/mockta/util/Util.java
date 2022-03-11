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

package codes.vps.mockta.util;

import lombok.NonNull;
import org.jose4j.base64url.Base64Url;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Util {

	public static String randomId() {

		byte[] id = new byte[12];
		ThreadLocalRandom.current().nextBytes(id);
		// using standard base64 is a bad idea because of pluses and slashes, which
		// may cause pain for URLs (even though they shouldn't but see
		// https://github.com/spring-projects/spring-framework/issues/15727)
		// return Base64.getEncoder().encodeToString(id);

		// jose4j to the rescue though.
		return Base64Url.encode(id);

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

	public static <S> void whenNotNull(@Nullable S val, @NonNull Consumer<S> fun) {
		whenNotNullOr(val, fun, null);
	}


	public static <S> void whenNotNull(@Nullable S val, @NonNull Consumer<S> fun, @Nullable Supplier<S> whenNull) {

		S v = val;
		if (v == null) {
			if (whenNull != null) {
				v = whenNull.get();
			}
		}
		if (v != null) {
			fun.accept(v);
		}

	}

	public static <S> void whenNotNullOr(@Nullable S val, @Nullable Consumer<S> fun, @Nullable Runnable whenNull) {
		if (val == null) {
			if (whenNull != null) {
				whenNull.run();
			}
		} else {
			if (fun != null) {
				fun.accept(val);
			}
		}
	}

	@Nullable
	public static <S, T> S ifNotNull(@Nullable T val, @NonNull Function<T, S> fun, @Nullable Supplier<S> whenNull) {
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

	@NonNull
	public static <S, T> S makeNotNull(@Nullable T val, @NonNull Function<T, S> fun, @NonNull Supplier<S> whenNull) {
		S ret = ifNotNull(val, fun, whenNull);
		if (ret == null) { throw new NullPointerException("not null must be produced"); }
		return ret;
	}

	@NonNull
	public static <T> T makeNotNull(@Nullable T val, @NonNull Supplier<T> whenNull) {
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
	public static RuntimeException doThrow(@NonNull Throwable e) {
		return doThrow0(e);
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> RuntimeException doThrow0(@NonNull Throwable e) throws E {
		throw (E) e;
	}

	public static String escapeForSqJsString(CharSequence s) {
		return escapeForJSString(s, '\'');
	}

	public static String escapeForJSString(CharSequence s, char quoteUsed) {

		// $TODO: Okta seem to escape A LOT of characters, using \x sequence.
		// It escapes like spaces and such. IDK why, probably a blanket security
		// measure. This should generally work fine.
		// We'll also screw up any code points that are >4 bytes long.

		if (s == null) { return "(null)"; }

		StringBuilder sb = new StringBuilder();
		int _l = s.length();
		for (int i=0; i<_l; i++ ) {

			char c = s.charAt(i);

			if (c == quoteUsed || c == '\\') {
				sb.append('\\');
				sb.append(c);
			} else if (c < 0x20) {
				sb.append(String.format("\\x%02x", (int) c));
			} else if (c >= 0x7f) {
				sb.append(String.format("\\u%04X", (int) c));
			} else {
				sb.append(c);
			}

		}

		return sb.toString();

	}

	/**
	 * Extracts result from a {@link Callable}, throwing any produced exception as
	 * a runtime exception.
	 * @param from get result from
	 * @return result from Callable
	 */
	public static <T> T reThrow(Callable<T> from) {
		try {
			return from.call();
		} catch (Exception e) {
			throw doThrow(e);
		}
	}

	/**
	 * Executes a {@link RunnableT}, throwing any produced exception as
	 * a runtime exception.
	 * @param r Runnable to execute
	 */
	public static void reThrow(RunnableT<? extends Throwable> r) {

		try {
			r.run();
		} catch (Throwable e) {
			throw doThrow(e);
		}

	}


	// https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/convert/support/ConversionUtils.java
	public static Class<?> getEnumType(Class<?> targetType) {
		Class<?> enumType = targetType;
		while (enumType != null && !enumType.isEnum()) {
			enumType = enumType.getSuperclass();
		}
		Assert.notNull(enumType, () -> "The target type " + targetType + " does not refer to an enum");
		return enumType;
	}


}
