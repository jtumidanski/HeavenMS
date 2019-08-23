package reactor


import scripting.reactor.ReactorActionManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap


class Reactor6701002 {
   ReactorActionManager rm

   def act() {
      int startId = 9400523
      MapleMonster mobObj
      MapleMap mapObj = rm.getMap()

      for (int i = 0; i < 7; i++ ) {
         mobObj = MapleLifeFactory.getMonster(startId + Math.floor(Math.random() * 3).intValue())
         mapObj.spawnMonsterOnGroundBelow(mobObj, rm.getReactor().getPosition())
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor6701002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6701002(rm: rm))
   return (Reactor6701002) getBinding().getVariable("reactor")
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