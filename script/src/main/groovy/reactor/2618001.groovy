package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager


class Reactor2618001 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      EventInstanceManager eim = rm.getEventInstance()

      int isAlcadno = eim.getIntProperty("isAlcadno")
      String reactorName = (isAlcadno == 0) ? "rnj32_out" : "jnr32_out"
      int reactorMap = (isAlcadno == 0) ? 926100202 : 926110202

      eim.getMapInstance(reactorMap).getReactorByName(reactorName).hitReactor(rm.getClient())
   }

   def touch() {

   }

   def release() {

   }
}

Reactor2618001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2618001(rm: rm))
   return (Reactor2618001) getBinding().getVariable("reactor")
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