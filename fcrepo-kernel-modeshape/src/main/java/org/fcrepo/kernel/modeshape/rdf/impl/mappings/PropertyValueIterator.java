/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.kernel.modeshape.rdf.impl.mappings;

import static com.google.common.collect.Iterators.singletonIterator;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

import org.fcrepo.kernel.api.exception.RepositoryRuntimeException;
import org.slf4j.Logger;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import java.util.Iterator;

/**
 * Iterate over all the values in a property or list of properties
 *
 * @author cabeer
 */
public class PropertyValueIterator extends AbstractIterator<Value> {
    private final Iterator<Property> properties;
    private Iterator<Value> currentValues;

    private static final Logger LOGGER = getLogger(PropertyValueIterator.class);

    public static long TOTAL_TIME = 0;
    public static long TOTAL_REQUESTS = 0;
    /**
     * Iterate through multiple properties' values
     * @param properties the properties
     */
    public PropertyValueIterator(final Iterator<Property> properties) {
        this.properties = properties;
        this.currentValues = null;
    }

    /**
     * Iterate through a property's values
     * @param property the properties
     */
    public PropertyValueIterator(final Property property) {
        this.properties = singletonIterator(property);
        this.currentValues = null;
    }

    @Override
    protected Value computeNext() {

        final long start = System.currentTimeMillis();
        try {
            if (currentValues != null && currentValues.hasNext()) {
                return currentValues.next();
            }
            if (properties.hasNext()) {
                final Property property = properties.next();

                if (property.isMultiple()) {
                    currentValues = Iterators.forArray(property.getValues());
                    if (currentValues.hasNext()) {
                        return currentValues.next();
                    }
                    return endOfData();
                }
                currentValues = null;
                return property.getValue();
            }

            return endOfData();
        } catch (final RepositoryException e) {
            throw new RepositoryRuntimeException(e);
        } finally {
            final long end = System.currentTimeMillis();
            TOTAL_REQUESTS++;
            TOTAL_TIME += end - start;
        }
    }
}
