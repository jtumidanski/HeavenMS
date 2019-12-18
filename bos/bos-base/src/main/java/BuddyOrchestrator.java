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
import rest.RestService;
import rest.ServerFactory;
import rest.UriBuilder;
import rest.master.character.Character;
import rest.master.character.CharactersResponse;
import tools.FilePrinter;

public class BuddyOrchestrator {
   public static void main(String[] args) throws IOException {
      PersistenceManager.construct("ms-buddy");
      URI uri = UriBuilder.host(RestService.BUDDY).uri();
      final HttpServer server = ServerFactory.create(uri);
      startup();
   }

   protected static void startup() {
      UriBuilder.service(RestService.MASTER).path("characters").getRestClient(CharactersResponse.class)
            .success(BuddyOrchestrator::onSyncSuccess)
            .failure(responseCode -> FilePrinter.printError(FilePrinter.BUDDY_ORCHESTRATOR, "Failed to sync characters with error " + responseCode))
            .get();
   }

   private static void onSyncSuccess(int returnCode, CharactersResponse response) {
      List<Character> masterCharacters = response.characters();
      List<Integer> myCharacters = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(entityManager -> myCharacters.addAll(CharacterProvider.getInstance().getAllChars(entityManager)));

      List<Integer> toDelete = myCharacters.parallelStream().filter(id -> masterCharacters.parallelStream().noneMatch(chara -> id.equals(chara.id()))).collect(Collectors.toList());
      List<Character> toAdd = masterCharacters.parallelStream().filter(chara -> !myCharacters.contains(chara.id())).collect(Collectors.toList());

      DatabaseConnection.getInstance().withConnection(entityManager ->
            DatabaseConnection.getInstance().thing(entityManager, em -> {
               toDelete.forEach(id -> CharacterAdministrator.getInstance().deleteCharacter(em, id));
               toAdd.forEach(chara -> CharacterAdministrator.getInstance().addCharacter(em, chara.accountId(), chara.id()));
            }));
   }
}
