package reactor

import scripting.reactor.ReactorActionManager
import server.maps.MapleMap
import tools.MasterBroadcaster
import tools.packet.statusinfo.ShowBunny

class Reactor9101000 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9300061, 1, 0, 0) // (0, 0) is temp position
      MapleMap map = rm.getClient().getPlayer().getMap()
      map.startMapEffect("Protect the Moon Bunny that's pounding the mill, and gather up 10 Moon Bunny's Rice Cakes!", 5120016, 7000)
      MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowBunny())

      //TODO
//      rm.getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.showHPQMoon());
//      rm.getClient().getPlayer().getMap().showAllMonsters();
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor9101000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9101000(rm: rm))
   return (Reactor9101000) getBinding().getVariable("reactor")
}

def act() {
   getReactor().act()
}

def hit() {
   getReactor().hit()
}

def touch() {
   getReactor().touch()
}

def release() {
   getReactor().release()
}