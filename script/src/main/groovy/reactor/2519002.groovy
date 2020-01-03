package reactor

import scripting.reactor.ReactorActionManager
import server.maps.MapleMap

import java.awt.*

class Reactor2519002 extends SimpleReactor {
   def act() {
      int denyWidth = 320, denyHeight = 150
      Point denyPos = rm.getReactor().position()
      Rectangle denyArea = new Rectangle((denyPos.getX() - denyWidth / 2).intValue(), (denyPos.getY() - denyHeight / 2).intValue(), denyWidth, denyHeight)

      rm.getReactor().getMap().setAllowSpawnPointInBox(false, denyArea)

      MapleMap map = rm.getReactor().getMap()
      if (map.getReactorByName("sMob1").getState() >= 1 && map.getReactorByName("sMob2").getState() >= 1 && map.getReactorByName("sMob4").getState() >= 1 && map.countMonsters() == 0) {
         rm.getEventInstance().showClearEffect(map.getId())
      }
   }
}

Reactor2519002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2519002(rm: rm))
   return (Reactor2519002) getBinding().getVariable("reactor")
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