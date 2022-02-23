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

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumConverter implements ConverterFactory<String, Enum> {

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return Util.reThrow(() -> new StringToEnum(Util.getEnumType(targetType)));
	}

	private static class StringToEnum<T extends Enum> implements Converter<String, T> {

		private final Map<String, T> nameToConstant = new HashMap<>();

		StringToEnum(Class<T> enumType) throws Exception {

			for (T constant : enumType.getEnumConstants()) {
				String name = constant.name();
				JsonProperty annotation = enumType.getField(name).getAnnotation(JsonProperty.class);
				if (annotation != null) {
					name = annotation.value();
				}
				nameToConstant.put(name, constant);
			}

		}

		@Override
		@Nullable
		public T convert(String source) {
			if (source.isEmpty()) {
				// It's an empty enum identifier: reset the enum value to null.
				return null;
			}
			return nameToConstant.get(source);
		}
	}

}
