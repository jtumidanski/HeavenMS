package reactor

import scripting.reactor.ReactorActionManager
import server.maps.MapleMap

import java.awt.*


class Reactor2519003 {
   ReactorActionManager rm

   def act() {
      int denyWidth = 320, denyHeight = 150
      Point denyPos = rm.getReactor().getPosition()
      Rectangle denyArea = new Rectangle((denyPos.getX() - denyWidth / 2).intValue(), (denyPos.getY() - denyHeight / 2).intValue(), denyWidth, denyHeight)

      MapleMap map = rm.getReactor().getMap()
      map.setAllowSpawnPointInBox(false, denyArea)

      if (map.getReactorByName("sMob1").getState() >= 1 && map.getReactorByName("sMob2").getState() >= 1 && map.getReactorByName("sMob3").getState() >= 1 && map.countMonsters() == 0) {
         rm.getEventInstance().showClearEffect(map.getId())
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2519003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2519003(rm: rm))
   return (Reactor2519003) getBinding().getVariable("reactor")
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