package client.command.commands.gm4;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.DatabaseConnection;
import database.administrator.PlayerLifeAdministrator;
import net.server.channel.Channel;
import server.life.MapleLifeFactory;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class PlayerMobCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !pmob <mob id> [<mob time>]");
         return;
      }

      int mapId = player.getMapId();
      int mobId = Integer.parseInt(params[0]);
      int mobTime = (params.length > 1) ? Integer.parseInt(params[1]) : -1;

      Point checkPosition = player.getMap().getGroundBelow(player.position());
      int xPosition = checkPosition.x;
      int yPosition = checkPosition.y;
      int fh = player.getMap().getFootholds().findBelow(checkPosition).id();

      MapleLifeFactory.getMonster(mobId).ifPresentOrElse(mob -> {
         if (mob.getName().equals("MISSINGNO")) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You have entered an invalid mob id.");
            return;
         }

         mob.position_$eq(checkPosition);
         mob.cy_$eq(yPosition);
         mob.rx0_$eq(xPosition + 50);
         mob.rx1_$eq(xPosition - 50);
         mob.fh_$eq(fh);

         DatabaseConnection.getInstance().withConnection(connection ->
               PlayerLifeAdministrator.getInstance().create(connection, mobId, 0, fh, yPosition, xPosition + 50, xPosition - 50, "m", xPosition, yPosition, player.getWorld(), mapId, mobTime, 0));

         for (Channel ch : player.getWorldServer().getChannels()) {
            MapleMap map = ch.getMapFactory().getMap(mapId);
            map.addMonsterSpawn(mob, mobTime, -1);
            map.addAllMonsterSpawn(mob, mobTime, -1);
         }

         player.yellowMessage("Player mob created.");
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You have entered an invalid mob id."));
   }
}