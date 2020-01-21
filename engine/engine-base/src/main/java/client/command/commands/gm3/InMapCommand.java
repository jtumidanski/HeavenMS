package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.SimpleMessage;

public class InMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      StringBuilder st = new StringBuilder();
      for (MapleCharacter chr : player.getMap().getCharacters()) {
         st.append(chr.getName()).append(" ");
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, SimpleMessage.from(st.toString()));
   }
}
