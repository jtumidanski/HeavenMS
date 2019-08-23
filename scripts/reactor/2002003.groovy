package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2002003 {
   ReactorActionManager rm

   def act() {
      rm.dropItems()

      EventInstanceManager eim = rm.getEventInstance()
      eim.setProperty("statusStg7", "1")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2002003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2002003(rm: rm))
   return (Reactor2002003) getBinding().getVariable("reactor")
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