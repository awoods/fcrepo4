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
package org.fcrepo.http.commons.responses;

import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.fcrepo.http.commons.domain.RDFMediaType.JSON_LD;
import static org.fcrepo.http.commons.domain.RDFMediaType.N3;
import static org.fcrepo.http.commons.domain.RDFMediaType.N3_ALT2;
import static org.fcrepo.http.commons.domain.RDFMediaType.NTRIPLES;
import static org.fcrepo.http.commons.domain.RDFMediaType.RDF_XML;
import static org.fcrepo.http.commons.domain.RDFMediaType.TURTLE;
import static org.fcrepo.http.commons.domain.RDFMediaType.TURTLE_X;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.fcrepo.http.commons.api.rdf.HttpResourceConverter;
import org.fcrepo.kernel.modeshape.FedoraResourceImpl;
import org.fcrepo.kernel.modeshape.rdf.converters.PropertyConverter;
import org.fcrepo.kernel.modeshape.rdf.converters.ValueConverter;
import org.fcrepo.kernel.modeshape.rdf.impl.LdpContainerRdfContext;
import org.fcrepo.kernel.modeshape.rdf.impl.mappings.PropertyValueIterator;
import org.slf4j.Logger;

/**
 * Provides serialization for streaming RDF results.
 *
 * @author ajs6f
 * @since Nov 19, 2013
 */
@Provider
@Produces({TURTLE, N3, N3_ALT2, RDF_XML, NTRIPLES, TEXT_PLAIN, TURTLE_X, JSON_LD})
public class RdfStreamProvider implements MessageBodyWriter<RdfNamespacedStream> {

    private static long TOTAL_TIME = 0;
    private static long TOTAL_REQUESTS = 0;

    private static final Logger LOGGER = getLogger(RdfStreamProvider.class);

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType,
            final Annotation[] annotations, final MediaType mediaType) {
        LOGGER.debug(
                "Checking to see if we can serialize type: {} to mimeType: {}",
                type.getName(), mediaType.toString());
        if (!RdfNamespacedStream.class.isAssignableFrom(type)) {
            return false;
        }
        if (mediaType.equals(TEXT_HTML_TYPE)
                || (mediaType.getType().equals("application") && mediaType
                        .getSubtype().equals("html"))) {
            LOGGER.debug("Was asked for an HTML mimeType, returning false.");
            return false;
        }
        LOGGER.debug("Assuming that this is an attempt to retrieve RDF, returning true.");
        return true;
    }

    @Override
    public long getSize(final RdfNamespacedStream t, final Class<?> type,
            final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        // We do not know how long the stream is
        return -1;
    }

    @Override
    public void writeTo(final RdfNamespacedStream nsStream, final Class<?> type,
        final Type genericType, final Annotation[] annotations,
        final MediaType mediaType,
        final MultivaluedMap<String, Object> httpHeaders,
        final OutputStream entityStream) {

        final long start = System.currentTimeMillis();
        TOTAL_TIME = 0;
        TOTAL_REQUESTS = 0;

        LOGGER.debug("Serializing an RdfStream to mimeType: {}", mediaType);
        final RdfStreamStreamingOutput streamOutput = new RdfStreamStreamingOutput(nsStream.stream,
                nsStream.namespaces, mediaType);
        streamOutput.write(entityStream);


        LOGGER.info("val-conv, time: {} | requests: {}", ValueConverter.TOTAL_TIME , ValueConverter.TOTAL_REQUESTS);
        LOGGER.info("val-conv-n4v, time: {}", ValueConverter.TOTAL_TIME_NODE_FOR_VAL);
        LOGGER.info("val-conv-subj, time: {}", ValueConverter.TOTAL_TIME_GRAPH);
        LOGGER.info("prop-conv, time: {}", PropertyConverter.TOTAL_TIME);
        LOGGER.info("prop-val-itr, time: {} | requests: {}",
                PropertyValueIterator.TOTAL_TIME ,
                PropertyValueIterator.TOTAL_REQUESTS);
        LOGGER.info("rsrc-get-kids, time: {}", FedoraResourceImpl.TOTAL_TIME);
        LOGGER.info("http-rsc-conv, time: {} | requests: {}",
                HttpResourceConverter.TOTAL_TIME,
                HttpResourceConverter.TOTAL_REQUESTS);
        LOGGER.info("ldp-con-ctxt, time: {} | requests: {}",
                LdpContainerRdfContext.TOTAL_TIME,
                LdpContainerRdfContext.TOTAL_REQUESTS);

        TOTAL_REQUESTS++;
        TOTAL_TIME += System.currentTimeMillis() - start;
        LOGGER.info("time: {} | requests: {}", TOTAL_TIME , TOTAL_REQUESTS);
    }
}
