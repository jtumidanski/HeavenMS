package reactor

import scripting.event.EventInstanceManager
import scripting.reactor.ReactorActionManager

class Reactor9400301 extends SimpleReactor {
   def act() {
      EventInstanceManager eim = rm.getEventInstance()
      eim.getEm().getIv().invokeFunction("snowmanSnackFake", eim)
   }
}

Reactor9400301 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9400301(rm: rm))
   return (Reactor9400301) getBinding().getVariable("reactor")
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