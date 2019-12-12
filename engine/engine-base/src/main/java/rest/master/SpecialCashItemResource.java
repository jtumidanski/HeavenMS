package rest.master;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.SpecialCashItemAdministrator;


@Path("cash/items/special")
public class SpecialCashItemResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(SpecialCashItem specialCashItem) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         int key = SpecialCashItemAdministrator.getInstance().create(entityManager, specialCashItem.sn(), specialCashItem.modifier(), specialCashItem.info());
         specialCashItem.id(key);
      });
      if (specialCashItem.id() == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(specialCashItem).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<SpecialCashItem> specialCashItems) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            specialCashItems.forEach(specialCashItem -> {
               int key = SpecialCashItemAdministrator.getInstance().create(entityManager, specialCashItem.sn(), specialCashItem.modifier(), specialCashItem.info());
               specialCashItem.id(key);
            }));
      if (specialCashItems.stream().anyMatch(specialCashItem -> specialCashItem.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(specialCashItems).build();
   }
}
