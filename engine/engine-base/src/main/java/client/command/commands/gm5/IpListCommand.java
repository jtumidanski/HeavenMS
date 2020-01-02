package client.command.commands.gm5;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import constants.game.GameConstants;
import net.server.Server;
import net.server.world.World;

public class IpListCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      StringBuilder str = new StringBuilder("Player-IP relation:");

      for (World w : Server.getInstance().getWorlds()) {
         Collection<MapleCharacter> chars = w.getPlayerStorage().getAllCharacters();

         if (!chars.isEmpty()) {
            str.append("\r\n").append(GameConstants.WORLD_NAMES[w.getId()]).append("\r\n");

            for (MapleCharacter chr : chars) {
               str.append("  ").append(chr.getName()).append(" - ").append(chr.getClient().getSession().getRemoteAddress()).append("\r\n");
            }
         }
      }

      c.getAbstractPlayerInteraction().npcTalk(22000, str.toString());
   }

}