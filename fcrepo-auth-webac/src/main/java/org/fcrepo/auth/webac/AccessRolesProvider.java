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
package org.fcrepo.auth.webac;

import java.util.Collection;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.modeshape.jcr.value.Path;

/**
 * Provides the effective access roles for authorization.
 *
 * @author Gregory Jansen
 * @author whikloj
 */
public interface AccessRolesProvider {

    /**
     * Get the roles assigned to this Node. Optionally search up the tree for the effective roles.
     *
     * @param node the subject Node
     * @param effective if true then search for effective roles
     * @return a set of roles for each principal
     */
    public Map<String, Collection<String>> getRoles(final Node node, final boolean effective);


    /**
     * Finds effective roles assigned to a path, using first real ancestor node.
     *
     * @param absPath the real or potential node path
     * @param session session
     * @return the roles assigned to each principal
     * @throws RepositoryException if PathNotFoundException can not handle
     */
    public Map<String, Collection<String>> findRolesForPath(final Path absPath,
            final Session session) throws RepositoryException;

}
