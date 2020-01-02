package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

import java.awt.*


class Reactor2002014 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 1, 100, 400, 15)

      EventInstanceManager eim = rm.getEventInstance()
      if (eim.getProperty("statusStgBonus") != "1") {
         rm.spawnNpc(2013002, new Point(46, 840))
         eim.setProperty("statusStgBonus", "1")
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2002014 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2002014(rm: rm))
   return (Reactor2002014) getBinding().getVariable("reactor")
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