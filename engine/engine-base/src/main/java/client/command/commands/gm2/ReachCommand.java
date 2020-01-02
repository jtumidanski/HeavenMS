package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReachCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !reach <player name>");
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         if (player.getClient().getChannel() != victim.getClient().getChannel()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + victim.getName() + "' is at channel " + victim.getClient().getChannel() + ".");
         } else {
            MapleMap map = victim.getMap();
            player.saveLocationOnWarp();
            player.forceChangeMap(map, map.findClosestPortal(victim.position()));
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Unknown player."));
   }
}
