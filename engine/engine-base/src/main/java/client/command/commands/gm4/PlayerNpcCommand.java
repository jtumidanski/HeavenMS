package client.command.commands.gm4;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class PlayerNpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("PNPC_ADD_COMMAND_SYNTAX"));
         return;
      }

      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);

      if (target.isPresent()) {
         if (!MaplePlayerNPC.spawnPlayerNPC(player.getMapId(), player.position(), target.get())) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PNPC_ADD_COMMAND_FAILURE"));
         }
      }
   }
}
