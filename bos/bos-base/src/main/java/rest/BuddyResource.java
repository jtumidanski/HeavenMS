package rest;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import client.database.administrator.BuddyAdministrator;
import client.database.provider.BuddyProvider;
import database.DatabaseConnection;
import rest.buddy.AddBuddy;
import rest.buddy.AddBuddyResponse;
import rest.buddy.AddBuddyResult;
import rest.buddy.Buddy;
import rest.buddy.GetBuddiesResponse;
import rest.buddy.UpdateBuddy;

@Path("buddies")
public class BuddyResource {
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBuddies(@QueryParam("characterId") Integer characterId) {
      if (characterId != null) {
         List<Buddy> buddies = new ArrayList<>();
         List<Buddy> pending = new ArrayList<>();

         DatabaseConnection.getInstance().withConnection(entityManager -> {
            BuddyProvider.getInstance().getInfoForBuddies(entityManager, characterId).forEach(result -> buddies.add(new Buddy(result.characterId(), result.group())));
            BuddyProvider.getInstance().getInfoForPendingBuddies(entityManager, characterId).forEach(result -> pending.add(new Buddy(result)));
            //BuddyAdministrator.getInstance().deletePendingForCharacter(entityManager, id);
         });

         return Response.ok().entity(new GetBuddiesResponse(buddies, pending)).build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteBuddies(@QueryParam("characterId") Integer characterId, @QueryParam("buddyId") Integer buddyId) {
      if (characterId != null && buddyId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> BuddyAdministrator.getInstance().deleteBuddy(entityManager, characterId, buddyId));
         return Response.noContent().build();
      } else if (characterId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> BuddyAdministrator.getInstance().deleteForCharacter(entityManager, characterId));
         return Response.noContent().build();
      } else if (buddyId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> BuddyAdministrator.getInstance().deleteByBuddy(entityManager, buddyId));
         return Response.noContent().build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response updateBuddy(@QueryParam("characterId") Integer characterId, @QueryParam("buddyId") Integer buddyId, UpdateBuddy updateBuddy) {
      if (characterId != null && buddyId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> {
            BuddyAdministrator.getInstance().updateBuddy(entityManager, characterId, buddyId, updateBuddy.pending());
         });
         return Response.ok().build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }


   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addBuddy(AddBuddy addBuddy) {
      AddBuddyResult result = DatabaseConnection.getInstance().withConnectionResult(entityManager -> {
         boolean alreadyRequested = BuddyProvider.getInstance().buddyIsPending(entityManager, addBuddy.referenceCharacterId(), addBuddy.addId(), addBuddy.group());
         if (alreadyRequested) {
            return AddBuddyResult.ALREADY_REQUESTED;
         }

         boolean atCapacity = BuddyProvider.getInstance().atCapacity(entityManager, addBuddy.referenceCharacterId());
         if (atCapacity) {
            return AddBuddyResult.FULL;
         }

         //TODO shortcut if group update.

         boolean buddyExists = BuddyProvider.getInstance().buddyExists(entityManager, addBuddy.addId());
         if (!buddyExists) {
            return AddBuddyResult.TARGET_CHARACTER_DOES_NOT_EXIST;
         }

         boolean buddyAlreadyRequested = BuddyProvider.getInstance().buddyIsPending(entityManager, addBuddy.addId(), addBuddy.referenceCharacterId(), addBuddy.group());
         if (buddyAlreadyRequested) {
            return AddBuddyResult.BUDDY_ALREADY_REQUESTED;
         }

         boolean buddyAtCapacity = BuddyProvider.getInstance().atCapacity(entityManager, addBuddy.addId());
         if (buddyAtCapacity) {
            return AddBuddyResult.BUDDY_FULL;
         }

         BuddyAdministrator.getInstance().addBuddy(entityManager, addBuddy.referenceCharacterId(), addBuddy.addId());
         BuddyAdministrator.getInstance().addBuddy(entityManager, addBuddy.addId(), addBuddy.referenceCharacterId());

         return AddBuddyResult.OK;
      }).orElseThrow();
      return Response.ok().entity(new AddBuddyResponse(result)).build();
   }
}