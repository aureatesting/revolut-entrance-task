package com.revolut.entrancetask.alexeyz;

import com.revolut.entrancetask.alexeyz.persistence.PersistenceUtil;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * Application start point
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/revolut/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // Create resource config that scans for JAX-RS resources and providers
        // in com.revolut.entrancetask.alexeyz package
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.revolut.entrancetask.alexeyz")
                .register(createJsonResolver());;

        // Ensure hibernate config is ok
        PersistenceUtil.getEntityManagerFactory();

        // Start grizzly
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static ContextResolver<MoxyJsonConfig> createJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        moxyJsonConfig.setNamespacePrefixMapper(Collections.singletonMap("http://www.w3.org/2001/XMLSchema-instance", "xsi"))
                .setNamespaceSeparator(':');

        return moxyJsonConfig.resolver();
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        //noinspection ResultOfMethodCallIgnored
        System.in.read();

        server.shutdownNow();
    }
}

