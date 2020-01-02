package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeonInfo;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class WarpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !warp <map id>");
         return;
      }

      try {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
         if (target == null) {
            player.yellowMessage("Map ID " + params[0] + " is invalid.");
            return;
         }

         if (!player.isAlive()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "This command cannot be used when you're dead.");
            return;
         }

         if (!player.isGM()) {
            if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOT_MIGRATE.check(player.getMap().getFieldLimit())) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "This command cannot be used in this map.");
               return;
            }
         }

         player.saveLocationOnWarp();
         player.changeMap(target, target.getRandomPlayerSpawnPoint());
      } catch (Exception ex) {
         player.yellowMessage("Map ID " + params[0] + " is invalid.");
      }
   }
}
