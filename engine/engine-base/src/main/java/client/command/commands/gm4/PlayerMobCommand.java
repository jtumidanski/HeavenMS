package client.command.commands.gm4;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.DatabaseConnection;
import database.administrator.PlayerLifeAdministrator;
import server.life.MapleLifeFactory;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class PlayerMobCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PLAYER_MOB_COMMAND_SYNTAX"));
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("INVALID_MONSTER_ID"));
            return;
         }

         mob.setPosition(checkPosition);
         mob.setCy(yPosition);
         mob.setRx0(xPosition + 50);
         mob.setRx1(xPosition - 50);
         mob.setFh(fh);

         DatabaseConnection.getInstance().withConnection(connection ->
               PlayerLifeAdministrator.getInstance().create(connection, mobId, 0, fh, yPosition, xPosition + 50, xPosition - 50, "m", xPosition, yPosition, player.getWorld(), mapId, mobTime, 0));

         player.getWorldServer().getChannels().stream()
               .map(channel -> channel.getMapFactory().getMap(mapId))
               .forEach(map -> {
                  map.addMonsterSpawn(mob, mobTime, -1);
                  map.addAllMonsterSpawn(mob, mobTime, -1);
               });

         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PLAYER_MOB_COMMAND_SUCCESS"));
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("INVALID_MONSTER_ID")));
   }
}