
package org.fcrepo.services;

import static org.fcrepo.services.PathService.getObjectJcrNodePath;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.security.SecureRandom;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.fcrepo.FedoraObject;
import org.fcrepo.utils.FedoraJcrTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ObjectService.class, ServiceHelpers.class})
public class ObjectServiceTest implements FedoraJcrTypes {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateObjectNode() throws Exception {
        final Node mockNode = mock(Node.class);
        Session mockSession = mock(Session.class);
        String testPath = getObjectJcrNodePath("foo");
        FedoraObject mockWrapper = new FedoraObject(mockNode);
        whenNew(FedoraObject.class).withArguments(mockSession, testPath)
                .thenReturn(mockWrapper);
        ObjectService testObj = new ObjectService();
        Node actual = testObj.createObjectNode(mockSession, "foo");
        assertEquals(mockNode, actual);
        verifyNew(FedoraObject.class).withArguments(mockSession, testPath);
    }

    @Test
    public void testCreateObject() throws Exception {
        final Node mockNode = mock(Node.class);
        Session mockSession = mock(Session.class);
        String testPath = getObjectJcrNodePath("foo");
        FedoraObject mockWrapper = new FedoraObject(mockNode);
        whenNew(FedoraObject.class).withArguments(any(Session.class),
                any(String.class)).thenReturn(mockWrapper);
        ObjectService testObj = new ObjectService();
        testObj.createObject(mockSession, "foo");
        verifyNew(FedoraObject.class).withArguments(mockSession, testPath);
    }

    @Test
    public void testGetObject() throws RepositoryException {
        Session mockSession = mock(Session.class);
        Session mockROSession = mock(Session.class);
        String testPath = getObjectJcrNodePath("foo");
        ObjectService testObj = new ObjectService();
        testObj.readOnlySession = mockROSession;
        testObj.getObject("foo");
        testObj.getObject(mockSession, "foo");
        verify(mockROSession).getNode(testPath);
        verify(mockSession).getNode(testPath);
    }

    @Test
    public void testGetObjectNode() throws RepositoryException {
        Session mockSession = mock(Session.class);
        Session mockROSession = mock(Session.class);
        String testPath = getObjectJcrNodePath("foo");
        ObjectService testObj = new ObjectService();
        testObj.readOnlySession = mockROSession;
        testObj.getObjectNode("foo");
        testObj.getObjectNode(mockSession, "foo");
        verify(mockROSession).getNode(testPath);
        verify(mockSession).getNode(testPath);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetObjectNames() throws RepositoryException {
        String objPath = getObjectJcrNodePath("");
        Session mockSession = mock(Session.class);
        Node mockRoot = mock(Node.class);
        Node mockObj = mock(Node.class);
        when(mockObj.getName()).thenReturn("foo");
        NodeIterator mockIter = mock(NodeIterator.class);
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.nextNode()).thenReturn(mockObj).thenThrow(
                IndexOutOfBoundsException.class);
        when(mockRoot.getNodes()).thenReturn(mockIter);
        when(mockSession.getNode(objPath)).thenReturn(mockRoot);
        ObjectService testObj = new ObjectService();
        testObj.readOnlySession = mockSession;
        Set<String> actual = testObj.getObjectNames();
        verify(mockSession).getNode(objPath);
        assertEquals(1, actual.size());
        assertEquals("foo", actual.iterator().next());
    }

    @Test
    public void testDeleteOBject() throws RepositoryException {
        String objPath = getObjectJcrNodePath("foo");
        Session mockSession = mock(Session.class);
        Node mockRootNode = mock(Node.class);
        Node mockObjectsNode = mock(Node.class);
        Node mockObjNode = mock(Node.class);
        when(mockSession.getRootNode()).thenReturn(mockRootNode);
        when(mockRootNode.getNode("objects")).thenReturn(mockObjectsNode);
        when(mockSession.getNode(objPath)).thenReturn(mockObjNode);
        PowerMockito.mockStatic(ServiceHelpers.class);
        ObjectService testObj = new ObjectService();
        testObj.deleteObject("foo", mockSession);
        verify(mockSession).getNode(objPath);
        verify(mockObjNode).remove();
    }
}
