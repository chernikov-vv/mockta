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

package codes.vps.mockta.util;

import java.util.function.Supplier;

public class OnDemand<T> implements Supplier<T>, AutoCloseable {

    private T it;
    private boolean made;
    private final OnDemandMaker<T> maker;

    public OnDemand(OnDemandMaker<T> maker) {
        this.maker = maker;
    }

    @Override
    public T get() {

        if (made) { return it; }
        it = Util.reThrow(maker::make);
        made = true;
        return it;

    }

    public T getOrNull() {
        if (made) { return it; }
        return null;
    }

    public void set(T t) {
        if (made) { throw new IllegalStateException("on-demand object already made!"); }
        made = true;
        it = t;
    }

    public void reset() {
        Util.reThrow(this::close);
    }

    public boolean wasCalled() {
        return made;
    }

    @Override
    public void close() throws Exception {
        if (it != null) {
            maker.close(it);
            it = null;
        }
        made = false;
    }

}
