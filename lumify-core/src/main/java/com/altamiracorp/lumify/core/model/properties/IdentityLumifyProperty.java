/*
 * Copyright 2014 Altamira Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.altamiracorp.lumify.core.model.properties;

/**
 * A LumifyProperty whose raw and SecureGraph types are the same.
 */
public class IdentityLumifyProperty<T> extends LumifyProperty<T, T> {
    /**
     * Create a new IdentityLumifyProperty.
     * @param key the property key
     */
    public IdentityLumifyProperty(final String key) {
        super(key);
    }

    @Override
    public T wrap(final T value) {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unwrap(final Object value) {
        return (T) value;
    }
}
