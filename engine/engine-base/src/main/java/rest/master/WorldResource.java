package rest.master;

import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ms.engine.rest.EventAttributes;
import com.ms.engine.rest.MapAttributes;
import com.ms.engine.rest.PortalAttributes;
import com.ms.engine.rest.builders.EventAttributesBuilder;
import com.ms.engine.rest.builders.MapAttributesBuilder;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;
import client.MapleCharacter;
import net.server.Server;
import net.server.channel.Channel;
import rest.CharacterAttributes;
import rest.InputBody;
import rest.NpcAttributes;
import rest.builders.NpcAttributesBuilder;
import scripting.event.EventManager;
import server.life.MapleNPC;
import server.maps.MapleMap;
import server.maps.MaplePortal;

@Path("worlds")
public class WorldResource {
   @GET
   @Path("/{id}/channels/{channelId}/events/{eventId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getEvent(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                            @PathParam("eventId") String eventId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      Channel channel = Server.getInstance().getChannel(worldId, channelId);
      if (channel != null) {
         EventManager eventManager = channel.getEventSM().getEventManager(eventId);
         if (eventManager != null) {
            resultBuilder.setStatus(Response.Status.OK);
            resultBuilder.addData(new ResultObjectBuilder(EventAttributes.class, eventId)
                  .setAttribute(new EventAttributesBuilder().setName(eventManager.getName())));
         }
      }

      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/channels/{channelId}/maps/{mapId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMap(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                          @PathParam("mapId") Integer mapId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      Channel channel = Server.getInstance().getChannel(worldId, channelId);
      if (channel != null) {
         MapleMap map = channel.getMapFactory().getMap(mapId);
         resultBuilder.setStatus(Response.Status.OK);
         resultBuilder.addData(new ResultObjectBuilder(MapAttributes.class, map.getId())
               .setAttribute(new MapAttributesBuilder()
                     .setCharactersInMap(map.getCharacters().stream().map(MapleCharacter::getId).collect(Collectors.toList()))));
      }

      return resultBuilder.build();
   }

   @PATCH
   @Path("/{id}/channels/{channelId}/maps/{mapId}/relationships/characters")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response changeMap(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                             @PathParam("mapId") Integer mapId,
                             InputBody<CharacterAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(inputBody.idAsInt())
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               character.changeMap(mapId);
            });
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/channels/{channelId}/maps/{mapId}/portals")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getPortals(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                              @PathParam("mapId") Integer mapId, @QueryParam("name") String portalName) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      Channel channel = Server.getInstance().getChannel(worldId, channelId);
      if (channel != null) {
         MapleMap map = channel.getMapFactory().getMap(mapId);
         if (portalName != null) {
            MaplePortal portal = map.getPortal(portalName);
            resultBuilder.setStatus(Response.Status.OK);
            resultBuilder.addData(new ResultObjectBuilder(PortalAttributes.class, portal.getId())
                  .setAttribute(new EventAttributesBuilder().setName(portal.getName())));
         }
      }

      return resultBuilder.build();
   }

   @PATCH
   @Path("/{id}/channels/{channelId}/maps/{mapId}/portals/{portalId}/relationships/characters")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response changeMap(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                             @PathParam("mapId") Integer mapId, @PathParam("portalId") Integer portalId,
                             InputBody<CharacterAttributes> inputBody) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);
      Server.getInstance()
            .getWorld(worldId)
            .getPlayerStorage()
            .getCharacterById(inputBody.idAsInt())
            .ifPresent(character -> {
               resultBuilder.setStatus(Response.Status.OK);
               character.changeMap(mapId, portalId);
            });
      return resultBuilder.build();
   }

   @GET
   @Path("/{id}/channels/{channelId}/maps/{mapId}/npcs/{npcId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getMapNpc(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                             @PathParam("mapId") Integer mapId, @PathParam("npcId") Integer npcId) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      Channel channel = Server.getInstance().getChannel(worldId, channelId);
      if (channel != null) {
         MapleMap map = channel.getMapFactory().getMap(mapId);
         MapleNPC npc = map.getNPCById(npcId);
         if (npc != null) {
            resultBuilder.setStatus(Response.Status.OK);
            resultBuilder.addData(new ResultObjectBuilder(NpcAttributes.class, npcId)
                  .setAttribute(new NpcAttributesBuilder()
                        .setX(npc.position().x)
                        .setY(npc.position().y)
                  )
            );
         }
      }

      return resultBuilder.build();
   }

   @DELETE
   @Path("/{id}/channels/{channelId}/maps/{mapId}/partyQuests")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response resetPartyQuest(@PathParam("id") Integer worldId, @PathParam("channelId") Integer channelId,
                                   @PathParam("mapId") Integer mapId,
                                   @DefaultValue("1") @QueryParam("difficult") Integer difficulty) {
      ResultBuilder resultBuilder = new ResultBuilder(Response.Status.NOT_FOUND);

      Channel channel = Server.getInstance().getChannel(worldId, channelId);
      if (channel != null) {
         resultBuilder.setStatus(Response.Status.NO_CONTENT);
         MapleMap map = channel.getMapFactory().getMap(mapId);
         map.resetPQ(difficulty);
      }

      return resultBuilder.build();
   }
}
