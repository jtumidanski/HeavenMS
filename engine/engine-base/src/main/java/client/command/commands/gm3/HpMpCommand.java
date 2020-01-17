package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class HpMpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      Optional<MapleCharacter> victim = Optional.of(player);
      int statUpdate = 1;

      if (params.length == 2) {
         victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         statUpdate = Integer.parseInt(params[1]);
      } else if (params.length == 1) {
         statUpdate = Integer.parseInt(params[0]);
      } else {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("HP_MP_COMMAND_SYNTAX"));
      }

      if (victim.isPresent()) {
         victim.get().updateHpMp(statUpdate);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
      }
   }
}
