package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class GiveVpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("GIVE_VP_COMMAND_SYNTAX"));
         return;
      }

      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isPresent()) {
         victim.get().getClient().addVotePoints(Integer.parseInt(params[1]));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GIVE_VP_COMMAND_SUCCESS"));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
      }
   }
}
