
package org.modaclouds.RESTGraphite;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import junit.framework.TestCase;


public class MainTest extends TestCase {

    private HttpServer httpServer;
    
    private WebResource r;

    public MainTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        System.setProperty("localhost", "localhost");
		System.setProperty("localport", "9998");
		System.setProperty("graphitehost", "ec2-50-16-53-139.compute-1.amazonaws.com");
		System.setProperty("graphiteport", "2003");
		
		Main.BASE_URI = Main.getBaseURI();
        
        //start the Grizzly2 web container 
        httpServer = Main.startServer();

        // create the client
        Client c = Client.create();
        r = c.resource(Main.BASE_URI);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        httpServer.stop();
    }

    public void testGraphiteMetricsService() {
    	String JSON = null;
    	try {
			JSON = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/JSON_3.0.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

    	ClientResponse responseMsg = r.path("graphiteMetricsService/sendMetrics").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, JSON);
    	
        assertEquals("Sent!", responseMsg.getEntity(String.class));
    }

    /**
     * Test if a WADL document is available at the relative path
     * "application.wadl".
     */
    public void testApplicationWadl() {
        String serviceWadl = r.path("application.wadl").
                accept(MediaTypes.WADL).get(String.class);
                
        assertTrue(serviceWadl.length() > 0);
    }
}
