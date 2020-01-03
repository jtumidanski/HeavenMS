package reactor


import scripting.reactor.ReactorActionManager

class Reactor2401001 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9300089)
   }
}

Reactor2401001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2401001(rm: rm))
   return (Reactor2401001) getBinding().getVariable("reactor")
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