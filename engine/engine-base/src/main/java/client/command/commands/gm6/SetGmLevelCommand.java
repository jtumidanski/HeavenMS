package client.command.commands.gm6;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class SetGmLevelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SET_GM_LEVEL_COMMAND_SYNTAX"));
         return;
      }

      int newLevel = Integer.parseInt(params[1]);
      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
      if (target.isPresent()) {
         target.get().setGMLevel(newLevel);
         target.get().getClient().setGMLevel(newLevel);

         MessageBroadcaster.getInstance().sendServerNotice(target.get(), ServerNoticeType.NOTICE, I18nMessage.from("SET_GM_LEVEL_COMMAND_SUCCESS_TARGET").with(newLevel));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, I18nMessage.from("SET_GM_LEVEL_COMMAND_SUCCESS_LOOPBACK").with(target, newLevel));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
      }
   }
}
