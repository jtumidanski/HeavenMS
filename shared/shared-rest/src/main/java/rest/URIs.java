package rest;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public final class URIs {
   public static final URI BASE = URI.create("http://localhost:8080/ms");

   public static final UriBuilder BUDDIES = UriBuilder.fromUri(BASE).path("bos").port(8081).path("buddies");

   public static final UriBuilder BUDDY_CHAR = UriBuilder.fromUri(BASE).path("bos").port(8081).path("characters");

   public static final UriBuilder MASTER_CHAR = UriBuilder.fromUri(BASE).path("master").path("characters");
}
