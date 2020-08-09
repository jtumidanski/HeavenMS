package server.life;

import java.awt.Point;

import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.packet.spawn.SpawnNPC;

public class MapleNPCFactory {
   public static void spawnNpc(int npcId, Point pos, MapleMap map) {
      MapleNPC npc = MapleLifeFactory.getNPC(npcId);
      npc.setPosition(pos);
      npc.setCy(pos.y);
      npc.setRx0(pos.x + 50);
      npc.setRx1(pos.x - 50);
      npc.setFh(map.getFootholds().findBelow(pos).id());
      map.addMapObject(npc);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new SpawnNPC(npc));
   }
}
