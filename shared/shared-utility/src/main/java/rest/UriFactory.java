package rest;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import config.YamlConfig;

public class UriFactory {

   public static UriBuilder create(Service service) {
      String host = "localhost";
      int port = 8080;
      String basePath = "master";

      switch (service) {
         case BUDDY:
            host = YamlConfig.config.server.BUDDY_MS_HOST;
            port = YamlConfig.config.server.BUDDY_MS_PORT;
            basePath = "bos";
            break;
         case MASTER:
            host = YamlConfig.config.server.HOST;
            port = YamlConfig.config.server.HOST_PORT;
            basePath = "master";
            break;
      }

      return UriBuilder.fromUri(URI.create("http://" + host + ":" + port + "/ms")).path(basePath);
   }

   public enum Service {
      MASTER,
      BUDDY
   }
}
