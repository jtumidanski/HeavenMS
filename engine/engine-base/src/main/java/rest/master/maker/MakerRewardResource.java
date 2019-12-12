package rest.master.maker;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.MakerRewardAdministrator;

@Path("maker/rewards")
public class MakerRewardResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(MakerReward makerReward) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            MakerRewardAdministrator.getInstance().create(entityManager, makerReward.item(), makerReward.reward(), makerReward.quantity(), makerReward.probability()));
      return Response.ok().entity(makerReward).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createGlobalDrop(List<MakerReward> makerRewards) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            makerRewards.forEach(makerReward ->
                  MakerRewardAdministrator.getInstance().create(entityManager, makerReward.item(), makerReward.reward(), makerReward.quantity(), makerReward.probability())));
      return Response.ok().entity(makerRewards).build();
   }
}
