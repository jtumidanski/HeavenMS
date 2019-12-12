package rest.master.drop;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.DropDataGlobalAdministrator;
import rest.master.drop.GlobalDrop;

@Path("global_drops")
public class GlobalDropResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(GlobalDrop globalDrop) {
      int key = DatabaseConnection.getInstance().withConnectionResult(entityManager ->
            DropDataGlobalAdministrator.getInstance().create(entityManager, globalDrop.continent(), globalDrop.itemId(),
                  globalDrop.minimumQuantity(), globalDrop.maximumQuantity(), globalDrop.questId(), globalDrop.chance(),
                  globalDrop.comment())).orElse(-1);
      if (key == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      globalDrop.id(key);
      return Response.ok().entity(globalDrop).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(List<GlobalDrop> globalDrops) {
      DatabaseConnection.getInstance().withConnection(entityManager -> globalDrops.forEach(globalDrop -> {
         int key = DropDataGlobalAdministrator.getInstance().create(entityManager, globalDrop.continent(), globalDrop.itemId(),
               globalDrop.minimumQuantity(), globalDrop.maximumQuantity(), globalDrop.questId(), globalDrop.chance(),
               globalDrop.comment());
         globalDrop.id(key);
      }));
      if (globalDrops.stream().anyMatch(globalDrop -> globalDrop.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(globalDrops).build();
   }
}
