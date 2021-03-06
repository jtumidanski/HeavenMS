package reactor


import scripting.reactor.ReactorActionManager

class Reactor2201000 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9300011, 10)
   }
}

Reactor2201000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2201000(rm: rm))
   return (Reactor2201000) getBinding().getVariable("reactor")
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