import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;

import database.PersistenceManager;
import rest.ServerFactory;
import rest.UriBuilder;
import rest.RestService;

public class CashShopOrchestrator {
   public static void main(String[] args) throws IOException {
      PersistenceManager.construct("ms-cashshop");
      URI uri = UriBuilder.service(RestService.CASH_SHOP).uri();
      final HttpServer server = ServerFactory.create(uri);
      System.out.println(String.format("Jersey app started with WADL available at "
            + "%s/application.wadl\nHit enter to stop it...", uri));

      System.in.read();
      server.shutdownNow();
   }
}
