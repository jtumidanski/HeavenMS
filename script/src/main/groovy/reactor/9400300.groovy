package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

class Reactor9400300 extends SimpleReactor {
   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      eim.getEm().getIv().invokeFunction("snowmanSnack", eim)
   }
}

Reactor9400300 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9400300(rm: rm))
   return (Reactor9400300) getBinding().getVariable("reactor")
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