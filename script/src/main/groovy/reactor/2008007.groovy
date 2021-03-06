package reactor


import scripting.reactor.ReactorActionManager
import server.maps.MapleMap
class Reactor2008007 extends SimpleReactor {
   def act() {
   }

   def hit() {
      MapleMap map = rm.getMap()
      map.moveEnvironment("trap" + rm.getReactor().getName()[5], 1)
   }
}

Reactor2008007 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2008007(rm: rm))
   return (Reactor2008007) getBinding().getVariable("reactor")
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