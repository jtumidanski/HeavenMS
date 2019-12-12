package rest.master.drop;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.ReactorDropAdministrator;
import rest.master.drop.ReactorDrop;


@Path(" drops/reactors")
public class ReactorDropResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(ReactorDrop reactorDrop) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         int key = ReactorDropAdministrator.getInstance().create(entityManager, reactorDrop.reactor(), reactorDrop.item(), reactorDrop.chance(), reactorDrop.quest());
         reactorDrop.id(key);
      });
      if (reactorDrop.id() == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(reactorDrop).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<ReactorDrop> reactorDrops) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            reactorDrops.forEach(reactorDrop -> {
               int key = ReactorDropAdministrator.getInstance().create(entityManager, reactorDrop.reactor(), reactorDrop.item(), reactorDrop.chance(), reactorDrop.quest());
               reactorDrop.id(key);
            }));
      if (reactorDrops.stream().anyMatch(reactorDrop -> reactorDrop.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(reactorDrops).build();
   }
}
