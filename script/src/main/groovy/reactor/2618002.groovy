package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2618002 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      EventInstanceManager eim = rm.getEventInstance()

      int isAlcadno = eim.getIntProperty("isAlcadno")
      String reactname = (isAlcadno == 0) ? "rnj31_out" : "jnr31_out"
      int reactmap = (isAlcadno == 0) ? 926100201 : 926110201

      eim.getMapInstance(reactmap).getReactorByName(reactname).hitReactor(rm.getClient())
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2618002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2618002(rm: rm))
   return (Reactor2618002) getBinding().getVariable("reactor")
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