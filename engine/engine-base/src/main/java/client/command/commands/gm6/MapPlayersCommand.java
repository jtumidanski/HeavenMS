package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.world.World;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MapPlayersCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      String names = "";
      int map = player.getMapId();
      for (World world : Server.getInstance().getWorlds()) {
         for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
            int curMap = chr.getMapId();
            String hp = Integer.toString(chr.getHp());
            String maxHp = Integer.toString(chr.getCurrentMaxHp());
            String name = chr.getName() + ": " + hp + "/" + maxHp;
            if (map == curMap) {
               names = names.equals("") ? name : (names + ", " + name);
            }
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Players on map " + map + ": " + names);
   }
}
