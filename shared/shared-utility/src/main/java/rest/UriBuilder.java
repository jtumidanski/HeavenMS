package rest;

import java.net.URI;

import config.YamlConfig;

public final class UriBuilder {

   private javax.ws.rs.core.UriBuilder builder;

   protected UriBuilder(RestService service) {
      String host = "localhost";
      int port = 8080;
      String basePath = "master";

      switch (service) {
         case BUDDY:
            host = YamlConfig.config.server.BUDDY_MS_HOST;
            port = YamlConfig.config.server.BUDDY_MS_PORT;
            basePath = "bos";
            break;
         case CASH_SHOP:
            host = YamlConfig.config.server.CASH_SHOP_MS_HOST;
            port = YamlConfig.config.server.CASH_SHOP_MS_PORT;
            basePath = "cos";
            break;
         case MASTER:
            host = YamlConfig.config.server.HOST;
            port = YamlConfig.config.server.HOST_PORT;
            basePath = "master";
            break;
      }

      builder = javax.ws.rs.core.UriBuilder.fromUri(URI.create("http://" + host + ":" + port + "/ms")).path(basePath);
   }

   public static UriBuilder service(RestService service) {
      return new UriBuilder(service);
   }

   public UriBuilder path(String path) {
      builder.path(path);
      return this;
   }

   public UriBuilder path(Integer path) {
         builder.path(Integer.toString(path));
      return this;
   }

   public UriBuilder queryParam(String name, Object... value) {
      builder.queryParam(name, value);
      return this;
   }

   public URI uri() {
      return builder.build();
   }

   public RestClient<Void> getRestClient() {
      return new RestClient<>(builder.build());
   }

   public <T> RestClient<T> getRestClient(Class<T> responseClass) {
      return new RestClient<>(builder.build(), responseClass);
   }
}
