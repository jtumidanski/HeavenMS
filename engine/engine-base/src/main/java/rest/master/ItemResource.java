package rest.master;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ms.engine.rest.QuestConsumableItemAttributes;
import com.ms.engine.rest.builders.QuestConsumableItemAttributesBuilder;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;
import rest.ItemAttributes;
import rest.builders.ItemAttributesBuilder;
import server.MapleItemInformationProvider;
import server.QuestConsItem;

@Path("items")
public class ItemResource {
   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getItem(@PathParam("id") Integer itemId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      String name = MapleItemInformationProvider.getInstance().getName(itemId);
      if (name != null) {
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(
               new ResultObjectBuilder(ItemAttributes.class, itemId)
                     .setAttribute(new ItemAttributesBuilder()
                           .setName(MapleItemInformationProvider.getInstance().getName(itemId))
                           .setQuestItem(MapleItemInformationProvider.getInstance().isQuestItem(itemId))
                           .setPickupRestricted(MapleItemInformationProvider.getInstance().isPickupRestricted(itemId))
                     ));
      }

      return resultBuilder.build();
   }

   @GET
   @Path("/questConsumables/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getQuestConsumablesItem(@PathParam("id") Integer itemId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      String name = MapleItemInformationProvider.getInstance().getName(itemId);
      if (name != null) {
         resultBuilder.setStatus(Response.Status.OK);
         QuestConsItem consItem = MapleItemInformationProvider.getInstance().getQuestConsumablesInfo(itemId);
         resultBuilder.addData(
               new ResultObjectBuilder(QuestConsumableItemAttributes.class, itemId)
                     .setAttribute(new QuestConsumableItemAttributesBuilder()
                           .setExperience(consItem.exp())
                           .setGrade(consItem.grade())
                     ));
      }

      return resultBuilder.build();
   }
}
