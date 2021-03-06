package reactor


import scripting.reactor.ReactorActionManager

class Reactor2112006 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 1, 500, 800)
   }
}

Reactor2112006 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2112006(rm: rm))
   return (Reactor2112006) getBinding().getVariable("reactor")
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