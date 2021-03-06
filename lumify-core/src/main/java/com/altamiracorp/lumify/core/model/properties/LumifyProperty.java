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

import com.altamiracorp.securegraph.Element;
import com.altamiracorp.securegraph.ElementMutation;
import com.altamiracorp.securegraph.Visibility;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Map;

/**
 * A LumifyProperty provides convenience methods for converting standard
 * property values to and from their raw types to the types required to
 * store them in a SecureGraph instance.
 * @param <TRaw> the raw value type for this property
 * @param <TGraph> the value type presented to SecureGraph for this property
 */
public abstract class LumifyProperty<TRaw, TGraph> {
    /**
     * The property key.
     */
    private final String key;

    /**
     * The raw conversion function.
     */
    private final Function<Object, TRaw> rawConverter;

    /**
     * Create a new LumifyProperty with the given key.
     * @param inKey the property key
     */
    protected LumifyProperty(final String inKey) {
        this.key = inKey;
        this.rawConverter = new RawConverter();
    }

    /**
     * Convert the raw value to an appropriate value for storage
     * in SecureGraph.
     * @param value the raw value
     * @return the SecureGraph value type representing the input value
     */
    public abstract TGraph wrap(final TRaw value);

    /**
     * Convert the SecureGraph value to its original raw type.
     * @param value the SecureGraph value; may or may not be of type TGraph
     * @return the raw value represented by the input SecureGraph value
     * @throws ClassCastException if the provided value cannot be unwrapped
     */
    public abstract TRaw unwrap(final Object value);

    /**
     * Get the property key for this property.
     * @return the property key
     */
    public final String getKey() {
        return key;
    }

    /**
     * Add a mutation to set this property to the provided value.
     * @param mutation the element mutation
     * @param value the new property value
     * @param visibility the property visibility
     */
    public final void setProperty(final ElementMutation<?> mutation, final TRaw value, final Visibility visibility) {
        mutation.setProperty(key, wrap(value), visibility);
    }

    /**
     * Add a mutation to set this property to the provided value.
     * @param mutation the element mutation
     * @param value the new property value
     * @param metadata the property metadata
     * @param visibility the property visibility
     */
    public final void setProperty(final ElementMutation<?> mutation, final TRaw value, final Map<String, Object> metadata,
            final Visibility visibility) {
        mutation.setProperty(key, wrap(value), metadata, visibility);
    }

    /**
     * Set this property on the provided element.
     * @param element the element
     * @param value the new property value
     * @param visibility the property visibility
     */
    public final void setProperty(final Element element, final TRaw value, final Visibility visibility) {
        element.setProperty(key, wrap(value), visibility);
    }

    /**
     * Set this property on the provided element.
     * @param element the element
     * @param value the new property value
     * @param metadata the property metadata
     * @param visibility the property visibility
     */
    public final void setProperty(final Element element, final TRaw value, final Map<String, Object> metadata,
            final Visibility visibility) {
        element.setProperty(key, wrap(value), metadata, visibility);
    }

    /**
     * Add a mutation to add a new value to this property.
     * @param mutation the element mutation
     * @param multiKey the multi-valued property key
     * @param value the new property value
     * @param visibility the property visibility
     */
    public final void addPropertyValue(final ElementMutation<?> mutation, final String multiKey, final TRaw value,
            final Visibility visibility) {
        mutation.addPropertyValue(multiKey, key, wrap(value), visibility);
    }

    /**
     * Add a mutation to add a new value to this property
     * @param mutation the element mutation
     * @param multiKey the multi-valued property key
     * @param value the new property value
     * @param metadata the property metadata
     * @param visibility the property visibility
     */
    public final void addPropertyValue(final ElementMutation<?> mutation, final String multiKey, final TRaw value,
            final Map<String, Object> metadata, final Visibility visibility) {
        mutation.addPropertyValue(multiKey, key, wrap(value), metadata, visibility);
    }

    /**
     * Get the value of this property from the provided Element.
     * @param element the element
     * @return the value of this property on the given Element or null if it is not configured
     */
    public final TRaw getPropertyValue(final Element element) {
        Object value = element != null ? element.getPropertyValue(key) : null;
        return value != null ? rawConverter.apply(value) : null;
    }

    /**
     * Get all values of this property from the provided Element.
     * @param element the element
     * @return an Iterable over the values of this property on the given Element
     */
    @SuppressWarnings("unchecked")
    public final Iterable<TRaw> getPropertyValues(final Element element) {
        Iterable<Object> values = element != null ? element.getPropertyValues(key) : null;
        return values != null ? Iterables.transform(values, rawConverter) : Collections.EMPTY_LIST;
    }

    /**
     * Function that converts the values returned by the Vertex.getProperty()
     * methods to the configured TRaw type.
     */
    private class RawConverter implements Function<Object, TRaw> {
        @Override
        @SuppressWarnings("unchecked")
        public TRaw apply(final Object input) {
            return unwrap(input);
        }
    }
}
