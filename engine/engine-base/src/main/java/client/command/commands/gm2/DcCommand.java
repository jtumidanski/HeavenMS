package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class DcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("DC_COMMAND_SYNTAX"));
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0])
            .or(() -> c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]))
            .ifPresent(victim -> {
               try {//sometimes bugged because the map = null
                  victim.getClient().disconnect(true, false);
                  player.getMap().removePlayer(victim);
               } catch (Exception e) {
                  e.printStackTrace();
               }

               if (player.gmLevel() < victim.gmLevel()) {
                  victim = player;
               }
               victim.getClient().disconnect(false, false);
            });
   }
}
