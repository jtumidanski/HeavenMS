package reactor


import scripting.reactor.ReactorActionManager
import tools.MaplePacketCreator


class Reactor9101000 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9300061, 1, 0, 0) // (0, 0) is temp position
      rm.getClient().getPlayer().getMap().startMapEffect("Protect the Moon Bunny that's pounding the mill, and gather up 10 Moon Bunny's Rice Cakes!", 5120016, 7000)
      rm.getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.bunnyPacket()) // Protect the Moon Bunny!

      //TODO
//      rm.getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.showHPQMoon());
//      rm.getClient().getPlayer().getMap().showAllMonsters();
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

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

def untouch() {
   getReactor().untouch()
}