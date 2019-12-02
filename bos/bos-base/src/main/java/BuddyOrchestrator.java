import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.glassfish.grizzly.http.server.HttpServer;

import database.DatabaseConnection;
import database.PersistenceManager;
import database.administrator.CharacterAdministrator;
import database.provider.CharacterProvider;
import rest.ServerFactory;
import rest.UriFactory;
import rest.master.Character;
import rest.master.CharactersResponse;
import tools.FilePrinter;
import tools.RestProvider;

public class BuddyOrchestrator {
   public static void main(String[] args) throws IOException {
      PersistenceManager.construct("ms-buddy");
      URI uri = UriFactory.create(UriFactory.Service.BUDDY).build();
      final HttpServer server = ServerFactory.create(uri);
      System.out.println(String.format("Jersey app started with WADL available at "
            + "%s/application.wadl\nHit enter to stop it...", uri));

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
