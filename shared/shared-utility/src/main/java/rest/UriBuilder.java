package rest;

import java.net.URI;

import config.YamlConfig;

public final class UriBuilder {

   private javax.ws.rs.core.UriBuilder builder;

   protected static String getHost(RestService service) {
      switch (service) {
         case BUDDY:
            return YamlConfig.config.server.BUDDY_MS_HOST;
         case CASH_SHOP:
            return YamlConfig.config.server.CASH_SHOP_MS_HOST;
         case MASTER:
            return YamlConfig.config.server.HOST;
      }
      return "localhost";
   }

   protected static int getPort(RestService service) {
      switch (service) {
         case BUDDY:
            return YamlConfig.config.server.BUDDY_MS_PORT;
         case CASH_SHOP:
            return YamlConfig.config.server.CASH_SHOP_MS_PORT;
         case MASTER:
            return YamlConfig.config.server.HOST_PORT;
      }
      return 8080;
   }

   protected static String getPath(RestService service) {
      switch (service) {
         case BUDDY:
            return "bos";
         case CASH_SHOP:
            return "cos";
         case MASTER:
            return "master";
      }
      return "master";
   }

   protected UriBuilder(RestService service) {
      String host = getHost(service);
      int port = getPort(service);
      String basePath = getPath(service);
      createBuilder(host, port, basePath);
   }

   protected void createBuilder(String host, int port, String basePath) {
      builder = javax.ws.rs.core.UriBuilder.fromUri(URI.create("http://" + host + ":" + port + "/ms")).path(basePath);
   }

   public static UriBuilder host(RestService service) {
      UriBuilder uriBuilder = new UriBuilder(service);
      uriBuilder.createBuilder("0.0.0.0", getPort(service), getPath(service));
      return uriBuilder;
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
