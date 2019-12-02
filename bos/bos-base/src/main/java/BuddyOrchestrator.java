import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jaxrs.Jaxrs2TypesModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import database.DatabaseConnection;
import database.PersistenceManager;
import database.administrator.CharacterAdministrator;
import database.provider.CharacterProvider;
import rest.UriFactory;
import rest.master.Character;
import rest.master.CharactersResponse;
import tools.FilePrinter;
import tools.RestProvider;

public class BuddyOrchestrator {

   private static final URI BASE = UriFactory.create(UriFactory.Service.BUDDY).build();

   public static HttpServer startServer() {
      final ResourceConfig rc = new ResourceConfig().packages("rest");
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new Jaxrs2TypesModule());
      mapper.registerModule(new ParameterNamesModule());
      mapper.registerModule(new JavaTimeModule());
      JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
      provider.setMapper(mapper);
      rc.register(provider);
      return GrizzlyHttpServerFactory.createHttpServer(BASE, rc);
   }

   public static void main(String[] args) throws IOException {
      PersistenceManager.construct("ms-buddy");
      final HttpServer server = startServer();
      System.out.println(String.format("Jersey app started with WADL available at "
            + "%s/application.wadl\nHit enter to stop it...", BASE));

      startup();

      System.in.read();
      server.shutdownNow();
   }

   protected static void startup() {
      RestProvider.getInstance().get(UriFactory.create(UriFactory.Service.MASTER).path("characters").build(), CharactersResponse.class,
            BuddyOrchestrator::onSyncSuccess,
            () -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to sync characters."));
   }

   private static void onSyncSuccess(CharactersResponse response) {
      List<Character> masterCharacters = response.characters();
      List<Integer> myCharacters = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(entityManager -> myCharacters.addAll(CharacterProvider.getInstance().getAllChars(entityManager)));

      List<Integer> toDelete = myCharacters.parallelStream().filter(id -> masterCharacters.parallelStream().noneMatch(chara -> id.equals(chara.id()))).collect(Collectors.toList());
      List<Character> toAdd = masterCharacters.parallelStream().filter(chara -> !myCharacters.contains(chara.id())).collect(Collectors.toList());

      DatabaseConnection.getInstance().withConnection(entityManager -> {
         DatabaseConnection.getInstance().thing(entityManager, em -> {
            toDelete.forEach(id -> CharacterAdministrator.getInstance().deleteCharacter(em, id));
            toAdd.forEach(chara -> CharacterAdministrator.getInstance().addCharacter(em, chara.accountId(), chara.id()));
         });
      });
   }
}
