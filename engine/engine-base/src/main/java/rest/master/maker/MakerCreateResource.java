package rest.master.maker;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.MakerCreateAdministrator;

@Path("maker/creation_requirements")
public class MakerCreateResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(MakerCreateRequirement requirement) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            MakerCreateAdministrator.getInstance().create(entityManager, requirement.id(), requirement.item(),
                  requirement.requiredLevel(), requirement.requiredMakerLevel(), requirement.requiredMeso(),
                  requirement.requiredItem(), requirement.requiredEquip(), requirement.catalyst(),
                  requirement.quantity(), requirement.tuc()));
      return Response.ok().entity(requirement).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(List<MakerCreateRequirement> requirements) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            requirements.forEach(requirement ->
                  MakerCreateAdministrator.getInstance().create(entityManager, requirement.id(), requirement.item(),
                        requirement.requiredLevel(), requirement.requiredMakerLevel(), requirement.requiredMeso(),
                        requirement.requiredItem(), requirement.requiredEquip(), requirement.catalyst(),
                        requirement.quantity(), requirement.tuc())));
      return Response.ok().entity(requirements).build();
   }
}
