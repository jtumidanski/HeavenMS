package rest.master.maker;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.MakerRecipeAdministrator;

@Path("maker/recipes")
public class MakerRecipeResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(MakerRecipe makerRecipe) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            MakerRecipeAdministrator.getInstance().create(entityManager, makerRecipe.item(), makerRecipe.requiredItem(), makerRecipe.count()));
      return Response.ok().entity(makerRecipe).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(List<MakerRecipe> makerRecipes) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            makerRecipes.forEach(makerRecipe ->
                  MakerRecipeAdministrator.getInstance().create(entityManager, makerRecipe.item(), makerRecipe.requiredItem(), makerRecipe.count())));
      return Response.ok().entity(makerRecipes).build();
   }
}
