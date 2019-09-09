package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2511000 {
   ReactorActionManager rm

   def act() {
      EventInstanceManager eim = rm.getPlayer().getEventInstance()
      int now = eim.getIntProperty("openedBoxes")
      int nextNum = now + 1
      eim.setIntProperty("openedBoxes", nextNum)

      rm.spawnMonster(9300109, 3)
      rm.spawnMonster(9300110, 5)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2511000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2511000(rm: rm))
   return (Reactor2511000) getBinding().getVariable("reactor")
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