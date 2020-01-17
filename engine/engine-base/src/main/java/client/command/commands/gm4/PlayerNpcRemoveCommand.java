package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class PlayerNpcRemoveCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PNPC_REMOVE_COMMAND_SYNTAX"));
         return;
      }
      client.getChannelServer().getPlayerStorage().getCharacterByName(params[0]).ifPresent(MaplePlayerNPC::removePlayerNPC);
   }
}
