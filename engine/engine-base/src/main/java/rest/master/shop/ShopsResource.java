package rest.master.shop;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.ShopAdministrator;
import database.administrator.ShopItemAdministrator;
import rest.master.Shop;


@Path("shops")
public class ShopsResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(Shop shop) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            ShopAdministrator.getInstance().create(entityManager, shop.id(), shop.npc()));
      return Response.ok().entity(shop).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<Shop> shops) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            shops.forEach(shop ->
                  ShopAdministrator.getInstance().create(entityManager, shop.id(), shop.npc())));
      return Response.ok().entity(shops).build();
   }

   @Path("{id}/items")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(@PathParam("id") Integer shopId, ShopItem shopItem) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         int key = ShopItemAdministrator.getInstance().create(entityManager, shopId, shopItem.item(), shopItem.price(), shopItem.pitch(), shopItem.position());
         shopItem.id(key);
      });
      if (shopItem.id() == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(shopItem).build();
   }

   @Path("{id}/items/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(@PathParam("id") Integer shopId, List<ShopItem> shopItems) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            shopItems.forEach(shopItem -> {
               int key = ShopItemAdministrator.getInstance().create(entityManager, shopId, shopItem.item(), shopItem.price(), shopItem.pitch(), shopItem.position());
               shopItem.id(key);
            }));
      if (shopItems.stream().anyMatch(shopItem -> shopItem.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(shopItems).build();
   }
}
