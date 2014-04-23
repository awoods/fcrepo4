/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.integration.kernel;

import org.fcrepo.kernel.FedoraResource;
import org.fcrepo.kernel.services.NodeService;
import org.fcrepo.kernel.services.NodeServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Andrew Woods
 *         Date: 4/23/14
 */
@ContextConfiguration({"/spring-test/repo.xml"})
public class NodeIterationIT extends AbstractIT {

    private static final Logger log = getLogger(NodeIterationIT.class);

    private NodeService service;

    @Inject
    private Repository repo;

    private List<String> paths = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        service = new NodeServiceImpl();
        loadData();
    }

    private void loadData() throws RepositoryException {
        Session session = null;
        try {
            session = repo.login();
            doLoadData(session);
        } finally {
            session.save();
            session.logout();
        }
    }

    private void doLoadData(final Session session) throws RepositoryException {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    String path = "/" + i + "/" + j + "/" + k;
                    paths.add(path);
                    service.findOrCreateObject(session, path);
                }
            }
        }
    }

    @After
    public void tearDown() throws RepositoryException {
        clearData();
    }

    private void clearData() throws RepositoryException {
        Session session = null;
        try {
            session = repo.login();
            doClearData(session);
        } finally {
            session.save();
            session.logout();
        }
    }

    private void doClearData(Session session) throws RepositoryException {
        for (String path : paths) {
            service.deleteObject(session, path);
        }
    }

    @Test
    public void testIterate() throws Exception {
        final Session session = repo.login();
        final FedoraResource object = service.getObject(session, "/");

        doIterate(object.getNode());

        session.logout();
    }

    private void doIterate(Node node) throws RepositoryException {
        log.info("path: {}", node.getPath());

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            doIterate(nodes.nextNode());
        }
    }

}
