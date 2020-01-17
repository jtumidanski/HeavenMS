package rest.master.world;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.server.Server;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

@Path("worlds")
public class WorldResource {
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}/characters/{characterId}/messages")
   public Response sendMessage(@PathParam("id") Integer worldId, @PathParam("characterId") Integer characterId, CharacterMessage message) {
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(characterId)
            .ifPresent(character ->
                  MessageBroadcaster.getInstance()
                        .sendServerNotice(character, ServerNoticeType.get(message.theType()), message.message()));
      return Response.ok().build();
   }
}
