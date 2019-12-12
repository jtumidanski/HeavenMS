package rest.master.character;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.provider.CharacterProvider;
import rest.master.character.CharactersResponse;
import rest.master.character.Character;
import database.DatabaseConnection;

@Path("characters")
public class CharacterResource {
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacters() {
      List<Character> characters = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(entityManager ->
            characters.addAll(
                  CharacterProvider.getInstance().getAllCharacters(entityManager).stream()
                        .map(result -> new Character(result.id(), result.accountId()))
                        .collect(Collectors.toList())));
      return Response.ok().entity(new CharactersResponse(characters)).build();
   }
}
