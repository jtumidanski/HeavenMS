package client.command.commands.gm4;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.DatabaseConnection;
import database.administrator.PlayerLifeAdministrator;
import net.server.channel.Channel;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import tools.I18nMessage;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.packet.spawn.SpawnNPC;

public class PnpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PNPC_COMMAND_SYNTAX"));
         return;
      }

      int mapId = player.getMapId();
      int npcId = Integer.parseInt(params[0]);
      if (player.getMap().containsNPC(npcId)) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PNPC_COMMAND_ALREADY_EXISTS"));
         return;
      }

      MapleNPC npc = MapleLifeFactory.getNPC(npcId);

      Point checkPosition = player.getMap().getGroundBelow(player.position());
      int xPosition = checkPosition.x;
      int yPosition = checkPosition.y;
      int fh = player.getMap().getFootholds().findBelow(checkPosition).id();

      if (npc != null && !npc.getName().equals("MISSINGNO")) {

         DatabaseConnection.getInstance().withConnection(connection ->
               PlayerLifeAdministrator.getInstance().create(connection, npcId, 0, fh, yPosition, xPosition + 50,
                     xPosition - 50, "n", xPosition, yPosition, player.getWorld(), mapId, -1, 0));

         for (Channel ch : player.getWorldServer().getChannels()) {
            npc = MapleLifeFactory.getNPC(npcId);
            npc.setPosition(checkPosition);
            npc.setCy(yPosition);
            npc.setRx0(xPosition + 50);
            npc.setRx1(xPosition - 50);
            npc.setFh(fh);

            MapleMap map = ch.getMapFactory().getMap(mapId);
            map.addMapObject(npc);
            MasterBroadcaster.getInstance().sendToAllInMap(map, new SpawnNPC(npc));
         }

         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PNPC_COMMAND_SUCCESS"));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PNPC_COMMAND_INVALID"));
      }
   }
}