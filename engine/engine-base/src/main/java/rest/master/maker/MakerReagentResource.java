package rest.master.maker;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.MakerReagentAdministrator;


@Path("maker/reagents")
public class MakerReagentResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(MakerReagent makerReagent) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            MakerReagentAdministrator.getInstance().create(entityManager, makerReagent.item(), makerReagent.stat(), makerReagent.value()));
      return Response.ok().entity(makerReagent).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<MakerReagent> makerReagents) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            makerReagents.forEach(makerReagent ->
                  MakerReagentAdministrator.getInstance().create(entityManager, makerReagent.item(), makerReagent.stat(), makerReagent.value())));
      return Response.ok().entity(makerReagents).build();
   }
}
