package rest.master;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;
import client.Skill;
import client.SkillFactory;
import rest.master.attributes.SkillAttributes;
import rest.master.attributes.builders.SkillAttributesBuilder;
import server.MapleItemInformationProvider;

@Path("skills")
public class SkillResource {
   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSkill(@PathParam("id") Integer itemId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      String name = MapleItemInformationProvider.getInstance().getName(itemId);
      if (name != null) {
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(
               new ResultObjectBuilder(SkillAttributes.class, itemId).setAttribute(new SkillAttributesBuilder()
                     .setBeginnerSkill(SkillFactory.getSkill(itemId).map(Skill::isBeginnerSkill).orElse(false))
               ));
      }

      return resultBuilder.build();
   }
}
