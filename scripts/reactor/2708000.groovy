package reactor


import scripting.reactor.ReactorActionManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap


class Reactor2708000 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      MapleMap mapObj = rm.getMap()
      //spawnJrBoss(mapObj.getMonsterById(8820019));
      //spawnJrBoss(mapObj.getMonsterById(8820020));
      //spawnJrBoss(mapObj.getMonsterById(8820021));
      //spawnJrBoss(mapObj.getMonsterById(8820022));
      //spawnJrBoss(mapObj.getMonsterById(8820023));
      mapObj.killMonster(8820000)
   }

   static def spawnJrBoss(MapleMonster mobObj) {
      mobObj.getMap().killMonster(mobObj.getId())
      int spawnid = mobObj.getId() - 17

      MapleMonster mob = MapleLifeFactory.getMonster(spawnid)
      mobObj.getMap().spawnMonsterOnGroundBelow(mob, mobObj.getPosition())
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2708000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2708000(rm: rm))
   return (Reactor2708000) getBinding().getVariable("reactor")
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