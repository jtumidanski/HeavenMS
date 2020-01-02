package reactor

import scripting.reactor.ReactorActionManager
import server.life.MapleLifeFactory
import server.maps.MapleMap

class Reactor6701000 {
   ReactorActionManager rm

   def act() {
      int startId = 9400523
      MapleMap mapObj = rm.getMap()

      for (int i = 0; i < 7; i++) {
         int monsterId = startId + Math.floor(Math.random() * 3).intValue()
         MapleLifeFactory.getMonster(monsterId).ifPresent({ monster -> mapObj.spawnMonsterOnGroundBelow(monster, rm.getReactor().position()) })
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor6701000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6701000(rm: rm))
   return (Reactor6701000) getBinding().getVariable("reactor")
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