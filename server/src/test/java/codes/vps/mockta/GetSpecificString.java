/*
 * Copyright (c) 2022 Pawel S. Veselov
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

import lombok.Getter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class GetSpecificString extends BaseMatcher<Object> implements Recorder<String> {

    @Getter
    private String recorded;

    private final String expected;

    public GetSpecificString(String expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object o) {
        boolean ok = expected.equals(o);
        if (!ok) {
            return false;
        }
        recorded = (String) o;
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Value(recorded) expected to be " + expected);
    }

}
