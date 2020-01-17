package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import server.maps.MaplePortal;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class JailCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("JAIL_COMMAND_SYNTAX"));
         return;
      }

      int minutesJailed = params.length >= 2 ? Integer.parseInt(params[1]) : 5;

      if (minutesJailed <= 0) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("JAIL_COMMAND_SYNTAX"));
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("JAIL_COMMAND_LOOPBACK").with(victim.getName(), minutesJailed));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("JAIL_COMMAND_EXTENDED").with(victim.getName(), minutesJailed));
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
