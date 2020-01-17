package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeonInfo;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class WarpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_COMMAND_SYNTAX"));
         return;
      }

      try {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
         if (target == null) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_COMMAND_INVALID_MAP").with(params[0]));
            return;
         }

         if (!player.isAlive()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, I18nMessage.from("COMMAND_CANNOT_BE_USED_WHEN_DEAD"));
            return;
         }

         if (!player.isGM()) {
            if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOT_MIGRATE.check(player.getMap().getFieldLimit())) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, I18nMessage.from("COMMAND_CANNOT_BE_USED_IN_MAP"));
               return;
            }
         }

         player.saveLocationOnWarp();
         player.changeMap(target, target.getRandomPlayerSpawnPoint());
      } catch (Exception ex) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("WARP_COMMAND_INVALID_MAP").with(params[0]));
      }
   }
}
