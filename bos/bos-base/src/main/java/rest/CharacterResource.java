package rest;

import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import client.database.administrator.BuddyAdministrator;
import client.database.administrator.CharacterAdministrator;
import client.database.provider.CharacterProvider;
import database.DatabaseConnection;
import rest.buddy.AddCharacter;
import rest.buddy.Character;
import rest.buddy.UpdateCharacter;

@Path("characters")
public class CharacterResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addCharacter(AddCharacter addCharacter) {
      DatabaseConnection.getInstance().withConnection(entityManager -> CharacterAdministrator.getInstance().addCharacter(entityManager, addCharacter.accountId(), addCharacter.characterId()));
      return Response.ok().build();
   }

   @PUT
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateCharacter(@PathParam("id") Integer id, UpdateCharacter updateCharacter) {
      DatabaseConnection.getInstance().withConnection(entityManager -> CharacterAdministrator.getInstance().updateCharacter(entityManager, id, updateCharacter.capacity()));
      return Response.ok().build();
   }

   @DELETE
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteCharacter(@PathParam("id") Integer id) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            DatabaseConnection.getInstance().thing(entityManager, em -> {
               BuddyAdministrator.getInstance().deleteForCharacter(em, id);
               BuddyAdministrator.getInstance().deleteByBuddy(em, id);
               CharacterAdministrator.getInstance().deleteCharacter(em, id);
            }));
      return Response.ok().build();
   }

   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCharacter(@PathParam("id") Integer id) {
      Optional<Character> character = DatabaseConnection.getInstance().withConnectionResult(entityManager -> CharacterProvider.getInstance().getCharacter(entityManager, id).orElse(null));
      if (character.isPresent()) {
         return Response.ok().entity(character.get()).build();
      } else {
         return Response.status(Response.Status.NOT_FOUND).build();
      }
   }
}
