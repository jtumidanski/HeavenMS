package rest;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jaxrs.Jaxrs2TypesModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class ServerFactory {
   public static HttpServer create(URI uri) {
      return create(uri, "rest");
   }

   public static HttpServer create(URI uri, String... packages) {
      final ResourceConfig resourceConfig = new ResourceConfig().packages(packages);
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new Jaxrs2TypesModule());
      mapper.registerModule(new ParameterNamesModule());
      mapper.registerModule(new JavaTimeModule());
      JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
      provider.setMapper(mapper);
      resourceConfig.register(provider);
      return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);
   }
}
