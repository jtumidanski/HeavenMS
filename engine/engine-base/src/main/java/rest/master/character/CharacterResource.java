package rest.master.character;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.provider.CharacterProvider;
import net.server.Server;
import server.MapleItemInformationProvider;

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

   @POST
   @Path("/{id}/fame")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response fameCharacter(@PathParam("id") Integer characterId, Integer amount) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character -> character.gainFame(amount));
      return Response.noContent().build();
   }

   @POST
   @Path("/{id}/meso")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response giveCharacterMeso(@PathParam("id") Integer characterId, Integer amount) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character -> character.gainMeso(amount));
      return Response.noContent().build();
   }

   @POST
   @Path("/{id}/exp")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response gainExp(@PathParam("id") Integer characterId, Integer amount) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character -> character.gainExp(amount));
      return Response.noContent().build();
   }

   @POST
   @Path("/{id}/buff")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response gainBuff(@PathParam("id") Integer characterId, Integer itemEffect) {
      int worldId = Server.getInstance().getCharacterWorld(characterId);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId).ifPresent(character -> MapleItemInformationProvider.getInstance().getItemEffect(itemEffect).applyTo(character));
      return Response.noContent().build();
   }
}
