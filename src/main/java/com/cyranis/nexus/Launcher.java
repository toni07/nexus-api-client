package com.cyranis.nexus;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.logging.Logger;

/**
 * @author toni07 on 16/11/2016.
 * Helped by https://github.com/marcelbirkner/restful-api-examples/blob/master/src/main/java/de/mb/rest/nexus/client/NexusTestClient.java
 */
public class Launcher {

	/**
	 * **************************************************************************************
	 * attributes
	 * **************************************************************************************
	 */
	private static Logger log = Logger.getLogger(Launcher.class.getSimpleName());
	private static String user = "user";
	private static String password = "pass";
	private static String url = "http://mynexus.url.com/nexus/";

	/**
	 * **************************************************************************************
	 * constructors
	 * **************************************************************************************
	 */

	/**
	 * **************************************************************************************
	 * methods
	 * **************************************************************************************
	 */
	public static void main(String[] args) throws Exception{

		final WebResource service = getService();
		log.info("Check that Nexus is running");
		final String nexusStatus = service.path("service").path("local").path("status").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class).toString();
		log.info(nexusStatus + "\n");

		log.info("GET Nexus Version");
		final String nexusVersion = service.path("service").path("local").path("status").accept(MediaType.APPLICATION_JSON).get(String.class).toString();
		log.info(nexusVersion + "\n");

		log.info("Get all repo targets");
		String repoTargets = service.path("service").path("local").path("repo_targets")
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		log.info(repoTargets);

		log.info("Get all artifact candidates");
		String artifactCandidates = service.path("service").path("local").path("lucene").path("search")
				.queryParam("g", "com.cyranis.dummy")
				.queryParam("a", "cyr-dum-proj")
				.queryParam("r", "cyr-releases")
				.queryParam("p", "war")
				.accept(MediaType.APPLICATION_JSON).get(String.class).toString();
		log.info(artifactCandidates);

		log.info("------ trying to download artifact ------");		//thanks to http://stackoverflow.com/questions/8928037/how-do-i-get-to-store-a-downloaded-file-with-java-and-jersey
		final ClientResponse response = service.path("service").path("local").path("artifact").path("maven").path("content")
				.queryParam("g", "com.cyranis.dummy")
				.queryParam("a", "cyr-dum-proj")
				.queryParam("r", "cyr-releases")
				.queryParam("v", "1.7.4.002")
				.queryParam("p", "war")
				.get(ClientResponse.class);
		final File s = response.getEntity(File.class);
		final File ff = new File("toto.war");
		s.renameTo(ff);
		final FileWriter fr = new FileWriter(s);
		fr.flush();
		log.info("	-- download ok! --	");
	}

	/**
	 *
	 * @return
	 */
	private static WebResource getService() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(user, password));
		return client.resource(getBaseURI());
	}

	/**
	 *
	 * @return
	 */
	private static URI getBaseURI() {
		return UriBuilder.fromUri(url).build();
	}

	/**
	 * **************************************************************************************
	 * getters & setters
	 * **************************************************************************************
	 */
}
