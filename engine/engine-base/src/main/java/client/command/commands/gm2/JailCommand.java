package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MaplePortal;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class JailCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !jail <player name> [<minutes>]");
         return;
      }

      int minutesJailed = params.length >= 2 ? Integer.parseInt(params[1]) : 5;

      if (minutesJailed <= 0) {
         player.yellowMessage("Syntax: !jail <player name> [<minutes>]");
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.addJailExpirationTime(minutesJailed * 60 * 1000);
         int mapId = 300000012;
         if (victim.getMapId() != mapId) {    // those gone to jail won't be changing map anyway
            MapleMap target = c.getChannelServer().getMapFactory().getMap(mapId);
            MaplePortal targetPortal = target.getPortal(0);
            victim.saveLocationOnWarp();
            victim.changeMap(target, targetPortal);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, victim.getName() + " was jailed for " + minutesJailed + " minutes.");
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, victim.getName() + "'s time in jail has been extended for " + minutesJailed + " minutes.");
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
   }
}
