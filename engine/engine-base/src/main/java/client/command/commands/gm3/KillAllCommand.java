package client.command.commands.gm3;

import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class KillAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleMap map = player.getMap();
      List<MapleMapObject> monsters = map.getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      int count = 0;

      for (MapleMapObject mapObject : monsters) {
         MapleMonster monster = (MapleMonster) mapObject;
         if (!monster.getStats().isFriendly() && !(monster.id() >= 8810010 && monster.id() <= 8810018)) {
            map.damageMonster(player, monster, Integer.MAX_VALUE);
            count++;
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Killed " + count + " monsters.");
   }
}
