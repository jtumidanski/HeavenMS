package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class SpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !sp [<player name>] <new sp>");
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "SP given.");
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
      }
   }
}
