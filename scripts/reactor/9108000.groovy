package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import server.maps.MapleMap
import server.maps.MapleReactor


class Reactor9108000 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      if (eim != null) {
         MapleReactor react = rm.getReactor().getMap().getReactorByName("fullmoon")
         int stage = (eim.getProperty("stage")).toInteger() + 1
         String newStage = stage.toString()
         eim.setProperty("stage", newStage)
         react.forceHitReactor((byte) (react.getState() + 1))
         if (eim.getProperty("stage") == "6") {
            rm.mapMessage(6, "Protect the Moon Bunny!!!")
            MapleMap map = eim.getMapInstance(rm.getReactor().getMap().getId())
            map.allowSummonState(true)
            map.spawnMonsterOnGroundBelow(9300061, -183, -433)
         }
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9108000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9108000(rm: rm))
   return (Reactor9108000) getBinding().getVariable("reactor")
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