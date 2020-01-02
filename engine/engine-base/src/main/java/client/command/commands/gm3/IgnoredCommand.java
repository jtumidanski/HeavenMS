package client.command.commands.gm3;

import client.processor.CharacterProcessor;
import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MapleLogger;

public class IgnoredCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Integer cid : MapleLogger.ignored) {
         player.yellowMessage(CharacterProcessor.getInstance().getNameById(cid) + " is being ignored.");
      }
   }
}
