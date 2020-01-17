package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class KillCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("KILL_COMMAND_SYNTAX"));
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.updateHpMp(0);
         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, player.getName() + " used !kill on " + victim.getName());
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
