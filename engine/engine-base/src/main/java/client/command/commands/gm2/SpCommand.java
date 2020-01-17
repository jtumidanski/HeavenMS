package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class SpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SP_COMMAND_SYNTAX"));
         return;
      }

      if (params.length == 1) {
         int newSp = Integer.parseInt(params[0]);
         if (newSp < 0) {
            newSp = 0;
         } else if (newSp > YamlConfig.config.server.MAX_AP) {
            newSp = YamlConfig.config.server.MAX_AP;
         }

         player.updateRemainingSp(newSp);
      } else {
         c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
            int newSp = Integer.parseInt(params[1]);
            if (newSp < 0) {
               newSp = 0;
            } else if (newSp > YamlConfig.config.server.MAX_AP) {
               newSp = YamlConfig.config.server.MAX_AP;
            }
            victim.updateRemainingSp(newSp);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SP_COMMAND_SUCCESS"));
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
      }
   }
}
