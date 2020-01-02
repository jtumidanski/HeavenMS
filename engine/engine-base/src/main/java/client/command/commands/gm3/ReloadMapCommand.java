package client.command.commands.gm3;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReloadMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleMap newMap = c.getChannelServer().getMapFactory().resetMap(player.getMapId());
      int callerId = c.getPlayer().getId();

      Collection<MapleCharacter> characters = player.getMap().getAllPlayers();

      for (MapleCharacter chr : characters) {
         chr.saveLocationOnWarp();
         chr.changeMap(newMap);
         if (chr.getId() != callerId)
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "You have been relocated due to map reloading. Sorry for the inconvenience.");
      }
      newMap.respawn();
   }
}
