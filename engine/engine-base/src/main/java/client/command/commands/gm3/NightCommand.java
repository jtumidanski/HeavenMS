package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class NightCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      player.getMap().broadcastNightEffect();
      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("COMMAND_DONE_MESSAGE"));
   }
}
