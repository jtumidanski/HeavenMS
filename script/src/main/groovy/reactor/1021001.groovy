package reactor


import scripting.reactor.ReactorActionManager

class Reactor1021001 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9300091)
   }
}

Reactor1021001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1021001(rm: rm))
   return (Reactor1021001) getBinding().getVariable("reactor")
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