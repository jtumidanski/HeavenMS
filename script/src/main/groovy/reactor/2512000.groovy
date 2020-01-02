package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager
import server.maps.MapleMap


class Reactor2512000 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getPlayer().getEventInstance()
      int now = eim.getIntProperty("openedBoxes")
      int nextNum = now + 1
      eim.setIntProperty("openedBoxes", nextNum)

      rm.dropItems(true, 1, 30, 60, 15)

      MapleMap map = rm.getMap()
      if (map.getMonsters().size() == 0 && passedGrindMode(map, eim)) {
         eim.showClearEffect(map.getId())
      }
   }

   static def passedGrindMode(MapleMap map, EventInstanceManager eim) {
      if (eim.getIntProperty("grindMode") == 0) {
         return true
      }
      return eim.activatedAllReactorsOnMap(map, 2511000, 2517999)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2512000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2512000(rm: rm))
   return (Reactor2512000) getBinding().getVariable("reactor")
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