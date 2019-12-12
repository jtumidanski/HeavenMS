package rest.master.monstercard;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import database.DatabaseConnection;
import database.administrator.MonsterCardAdministrator;


@Path("monsters/cards")
public class MonsterCardResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response create(MonsterCard monsterCard) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         int key = MonsterCardAdministrator.getInstance().create(entityManager, monsterCard.card(), monsterCard.mob());
         monsterCard.id(key);
      });

      if (monsterCard.id() == -1) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(monsterCard).build();
   }

   @Path("/bulk")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createBulk(List<MonsterCard> monsterCards) {
      DatabaseConnection.getInstance().withConnection(entityManager ->
            monsterCards.forEach(monsterCard -> {
               int key = MonsterCardAdministrator.getInstance().create(entityManager, monsterCard.card(), monsterCard.mob());
               monsterCard.id(key);
            })
      );
      if (monsterCards.stream().anyMatch(monsterCard -> monsterCard.id() == -1)) {
         return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.ok().entity(monsterCards).build();
   }
}
