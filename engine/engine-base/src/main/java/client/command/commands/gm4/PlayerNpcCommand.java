package client.command.commands.gm4;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class PlayerNpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !playernpc <player name>");
         return;
      }

      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);

      if (target.isPresent()) {
         if (!MaplePlayerNPC.spawnPlayerNPC(player.getMapId(), player.position(), target.get())) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Could not deploy PlayerNPC. Either there's no room available here or depleted out script ids to use.");
         }
      }
   }
}
