package rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.BuddyAdministrator;
import database.administrator.CharacterAdministrator;
import database.provider.BuddyProvider;
import database.provider.CharacterProvider;
import rest.buddy.AddBuddy;
import rest.buddy.AddBuddyResponse;
import rest.buddy.AddBuddyResult;
import rest.buddy.AddCharacter;
import rest.buddy.Buddy;
import rest.buddy.Character;
import rest.buddy.GetBuddiesResponse;
import rest.buddy.UpdateBuddy;
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

   @PATCH
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

   @GET
   @Path("/{id}/buddies")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBuddies(@PathParam("id") Integer characterId) {
      if (characterId != null) {
         List<Buddy> buddies = new ArrayList<>();
         List<Buddy> pending = new ArrayList<>();

         DatabaseConnection.getInstance().withConnection(entityManager -> {
            BuddyProvider.getInstance().getInfoForBuddies(entityManager, characterId).forEach(result -> buddies.add(new Buddy(result.characterId(), result.group())));
            BuddyProvider.getInstance().getInfoForPendingBuddies(entityManager, characterId).forEach(result -> pending.add(new Buddy(result, "n/a")));
            //BuddyAdministrator.getInstance().deletePendingForCharacter(entityManager, id);
         });

         return Response.ok().entity(new GetBuddiesResponse(buddies, pending)).build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }


   @POST
   @Path("/{id}/buddies")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addBuddy(@PathParam("id") Integer characterId, AddBuddy addBuddy) {
      AddBuddyResult result = DatabaseConnection.getInstance().withConnectionResult(entityManager -> {
         boolean alreadyRequested = BuddyProvider.getInstance().buddyIsPending(entityManager, characterId, addBuddy.addId(), addBuddy.group());
         if (alreadyRequested) {
            return AddBuddyResult.ALREADY_REQUESTED;
         }

         boolean atCapacity = BuddyProvider.getInstance().atCapacity(entityManager, characterId);
         if (atCapacity) {
            return AddBuddyResult.FULL;
         }

         boolean inOtherGroup = BuddyProvider.getInstance().buddyIsInOtherGroup(entityManager, characterId, addBuddy.addId(), addBuddy.group());
         if (inOtherGroup) {
            BuddyAdministrator.getInstance().updateBuddy(entityManager, characterId, addBuddy.addId(), addBuddy.group());
            return AddBuddyResult.OK;
         }

         boolean buddyExists = BuddyProvider.getInstance().buddyExists(entityManager, addBuddy.addId());
         if (!buddyExists) {
            return AddBuddyResult.TARGET_CHARACTER_DOES_NOT_EXIST;
         }

         boolean buddyAlreadyRequested = BuddyProvider.getInstance().buddyIsPending(entityManager, addBuddy.addId(), characterId, addBuddy.group());
         if (buddyAlreadyRequested) {
            return AddBuddyResult.BUDDY_ALREADY_REQUESTED;
         }

         boolean buddyAtCapacity = BuddyProvider.getInstance().atCapacity(entityManager, addBuddy.addId());
         if (buddyAtCapacity) {
            return AddBuddyResult.BUDDY_FULL;
         }

         BuddyAdministrator.getInstance().addBuddy(entityManager, characterId, addBuddy.addId(), addBuddy.group(), false);
         BuddyAdministrator.getInstance().addBuddy(entityManager, addBuddy.addId(), characterId, addBuddy.group(), true);

         return AddBuddyResult.OK;
      }).orElseThrow();
      return Response.created(URI.create("")).entity(new AddBuddyResponse(result)).build();
   }

   @PATCH
   @Path("/{id}/buddies/{buddyId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateBuddy(@PathParam("id") Integer characterId, @PathParam("buddyId") Integer buddyId, UpdateBuddy updateBuddy) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         BuddyAdministrator.getInstance().updateBuddy(entityManager, characterId, buddyId, updateBuddy.pending(), updateBuddy.responseRequired());
      });
      return Response.ok().build();
   }

   @DELETE
   @Path("/{id}/buddies")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteBuddies(@PathParam("id") Integer characterId) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         BuddyAdministrator.getInstance().deleteForCharacter(entityManager, characterId);
         BuddyAdministrator.getInstance().deleteByBuddy(entityManager, characterId);
      });
      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}/buddies/{buddyId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteBuddy(@PathParam("id") Integer characterId, @PathParam("buddyId") Integer buddyId) {
      DatabaseConnection.getInstance().withConnection(entityManager -> BuddyAdministrator.getInstance().deleteBuddy(entityManager, characterId, buddyId));
      DatabaseConnection.getInstance().withConnection(entityManager -> BuddyAdministrator.getInstance().deleteBuddy(entityManager, buddyId, characterId));
      return Response.noContent().build();
   }
}
