import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;

import database.PersistenceManager;
import rest.RestService;
import rest.ServerFactory;
import rest.UriBuilder;

public class CashShopOrchestrator {
   public static void main(String[] args) throws IOException {
      PersistenceManager.construct("ms-cashshop");
      URI uri = UriBuilder.host(RestService.CASH_SHOP).uri();
      final HttpServer server = ServerFactory.create(uri);
   }
}
