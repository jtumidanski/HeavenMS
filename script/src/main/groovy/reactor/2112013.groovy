package reactor


import scripting.reactor.ReactorActionManager

class Reactor2112013 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 1, 125, 175)
   }
}

Reactor2112013 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2112013(rm: rm))
   return (Reactor2112013) getBinding().getVariable("reactor")
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