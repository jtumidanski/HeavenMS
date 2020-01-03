package reactor


import scripting.reactor.ReactorActionManager


class Reactor2001 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 8, 15, 1)
   }
}

Reactor2001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2001(rm: rm))
   return (Reactor2001) getBinding().getVariable("reactor")
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