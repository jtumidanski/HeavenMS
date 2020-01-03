package server.life;

import java.awt.Point;

import server.maps.MapleMap;
import tools.MasterBroadcaster;
import tools.packet.spawn.SpawnNPC;

public class MapleNPCFactory {
   public static void spawnNpc(int npcId, Point pos, MapleMap map) {
      MapleNPC npc = MapleLifeFactory.getNPC(npcId);
      npc.position_$eq(pos);
      npc.cy_$eq(pos.y);
      npc.rx0_$eq(pos.x + 50);
      npc.rx1_$eq(pos.x - 50);
      npc.fh_$eq(map.getFootholds().findBelow(pos).id());
      map.addMapObject(npc);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new SpawnNPC(npc));
   }
}
