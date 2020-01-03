package reactor


import scripting.reactor.ReactorActionManager

class Reactor2221004 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9500400)
   }
}

Reactor2221004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221004(rm: rm))
   return (Reactor2221004) getBinding().getVariable("reactor")
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