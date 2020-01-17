package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ReachCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("REACH_COMMAND_SYNTAX"));
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         if (player.getClient().getChannel() != victim.getClient().getChannel()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("REACH_COMMAND_RESULT").with(victim.getName(), victim.getClient().getChannel()));
         } else {
            MapleMap map = victim.getMap();
            player.saveLocationOnWarp();
            player.forceChangeMap(map, map.findClosestPortal(victim.position()));
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
