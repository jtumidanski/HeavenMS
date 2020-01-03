package reactor


import scripting.reactor.ReactorActionManager

class Reactor2612003 extends SimpleReactor {
   def act() {
      rm.sprayItems()
   }
}

Reactor2612003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612003(rm: rm))
   return (Reactor2612003) getBinding().getVariable("reactor")
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