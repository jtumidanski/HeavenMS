package reactor


import scripting.reactor.ReactorActionManager

class Reactor8001000 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9400112, 1, 420, 160)
   }
}

Reactor8001000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor8001000(rm: rm))
   return (Reactor8001000) getBinding().getVariable("reactor")
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