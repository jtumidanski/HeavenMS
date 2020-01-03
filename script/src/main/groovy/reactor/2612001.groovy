package reactor


import scripting.reactor.ReactorActionManager

class Reactor2612001 extends SimpleReactor {
   def act() {
      rm.sprayItems()
   }
}

Reactor2612001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612001(rm: rm))
   return (Reactor2612001) getBinding().getVariable("reactor")
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