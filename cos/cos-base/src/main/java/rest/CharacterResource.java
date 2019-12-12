package rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.GiftAdministrator;
import database.administrator.WishListAdministrator;
import database.provider.GiftProvider;
import database.provider.WishListProvider;
import rest.cashshop.Gift;
import rest.cashshop.GiftsResponse;
import rest.cashshop.WishListItem;
import rest.cashshop.WishListResponse;

@Path("characters")
public class CharacterResource {

   @GET
   @Path("/{id}/wish-list/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getWishListItems(@PathParam("id") Integer characterId) {
      if (characterId != null) {
         List<WishListItem> wishListItems = new ArrayList<>();
         DatabaseConnection.getInstance().withConnection(entityManager ->
               WishListProvider.getInstance().getWishListSn(entityManager, characterId).forEach(id -> wishListItems.add(new WishListItem(id))));
         return Response.ok().entity(new WishListResponse(wishListItems)).build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @DELETE
   @Path("/{id}/wish-list/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteWishListItems(@PathParam("id") Integer characterId) {
      if (characterId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> WishListAdministrator.getInstance().deleteForCharacter(entityManager, characterId));
         return Response.noContent().build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @POST
   @Path("/{id}/wish-list/items")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addWishListItem(@PathParam("id") Integer characterId, WishListItem item) {
      DatabaseConnection.getInstance().withConnection(entityManager -> WishListAdministrator.getInstance().addForCharacter(entityManager, characterId, Collections.singletonList(item.id())));
      return Response.created(URI.create("")).entity(item).build();
   }


   @POST
   @Path("/{id}/gifts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response addGift(@PathParam("id") Integer characterId, Gift item) {
      DatabaseConnection.getInstance().withConnection(entityManager -> GiftAdministrator.getInstance().createGift(entityManager, characterId, item.from(), item.message(), item.sn(), item.ringId()));
      return Response.created(URI.create("")).entity(item).build();
   }

   @GET
   @Path("/{id}/gifts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getGifts(@PathParam("id") Integer characterId) {
      if (characterId != null) {
         List<Gift> gifts = new ArrayList<>();
         DatabaseConnection.getInstance().withConnection(entityManager ->
               GiftProvider.getInstance().getGiftsForCharacter(entityManager, characterId).forEach(data -> gifts.add(new Gift(data.from(), data.message(), data.sn(), data.sn()))));
         return Response.ok().entity(new GiftsResponse(gifts)).build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }

   @DELETE
   @Path("/{id}/gifts")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response deleteGifts(@PathParam("id") Integer characterId) {
      if (characterId != null) {
         DatabaseConnection.getInstance().withConnection(entityManager -> GiftAdministrator.getInstance().deleteAllGiftsForCharacter(entityManager, characterId));
         return Response.noContent().build();
      }
      return Response.status(Response.Status.NOT_FOUND).build();
   }
}
