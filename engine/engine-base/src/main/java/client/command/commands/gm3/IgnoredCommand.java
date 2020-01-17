package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.processor.CharacterProcessor;
import tools.MapleLogger;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class IgnoredCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Integer cid : MapleLogger.ignored) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("IGNORE_COMMAND_SUCCESS").with(CharacterProcessor.getInstance().getNameById(cid)));
      }
   }
}
