package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2512001 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getPlayer().getEventInstance()
      int now = eim.getIntProperty("openedChests")
      int nextNum = now + 1
      eim.setIntProperty("openedChests", nextNum)
      rm.sprayItems(true, 1, 50, 100, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2512001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2512001(rm: rm))
   return (Reactor2512001) getBinding().getVariable("reactor")
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