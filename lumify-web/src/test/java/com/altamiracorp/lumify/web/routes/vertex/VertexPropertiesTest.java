package com.altamiracorp.lumify.web.routes.vertex;

import com.altamiracorp.lumify.web.routes.RouteTestBase;
import com.altamiracorp.securegraph.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class VertexPropertiesTest extends RouteTestBase {
    private final String ID = "40004";
    private final String TITLE = "testVertex";
    private final String AUTHOR = "lumify";
    private final String SUBTYPE = "28";

    private VertexProperties vertexProperties;

    @Mock
    private Graph mockGraph;

    @Before
    @Override
    public void setUp() throws Exception {
//        super.setUp();
        vertexProperties = new VertexProperties(mockGraph);
    }

    @Test
    public void testHandle() throws Exception {
        // TODO rewrite this test for secure graph!!!
//        when(mockRequest.getAttribute("graphVertexId")).thenReturn(ID);
//
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put(PropertyName.TITLE.toString(), TITLE);
//        properties.put(PropertyName.AUTHOR.toString(), AUTHOR);
//        properties.put(PropertyName.CONCEPT_TYPE.toString(), SUBTYPE);
//
//        when(mockGraphRepository.getVertexProperties(ID, mockUser)).thenReturn(properties);
//
//        vertexProperties.handle(mockRequest, mockResponse, mockHandlerChain);
//
//        JSONObject response = new JSONObject(responseStringWriter.getBuffer().toString());
//        assertEquals(ID, response.getString("id"));
//        assertTrue(response.getJSONObject("properties").length() > 0);
//        assertEquals(TITLE, response.getJSONObject("properties").getString(PropertyName.TITLE.toString()));
//        assertEquals(SUBTYPE, response.getJSONObject("properties").getString(PropertyName.CONCEPT_TYPE.toString()));
//        assertEquals(AUTHOR, response.getJSONObject("properties").getString(PropertyName.AUTHOR.toString()));
    }
}
