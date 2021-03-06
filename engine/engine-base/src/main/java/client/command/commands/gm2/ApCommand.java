package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ApCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("AP_COMMAND_SYNTAX"));
         return;
      }

      if (params.length < 2) {
         int newAp = Integer.parseInt(params[0]);
         if (newAp < 0) {
            newAp = 0;
         } else if (newAp > YamlConfig.config.server.MAX_AP) {
            newAp = YamlConfig.config.server.MAX_AP;
         }

         player.changeRemainingAp(newAp, false);
      } else {
         c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
            int newAp = Integer.parseInt(params[1]);
            if (newAp < 0) {
               newAp = 0;
            } else if (newAp > YamlConfig.config.server.MAX_AP) {
               newAp = YamlConfig.config.server.MAX_AP;
            }
            victim.changeRemainingAp(newAp, false);
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
      }
   }
}
