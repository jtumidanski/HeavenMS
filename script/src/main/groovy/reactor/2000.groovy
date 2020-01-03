package reactor


import scripting.reactor.ReactorActionManager


class Reactor2000 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 8, 15, 1)
   }
}

Reactor2000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2000(rm: rm))
   return (Reactor2000) getBinding().getVariable("reactor")
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