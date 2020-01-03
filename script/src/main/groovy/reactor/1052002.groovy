package reactor


import scripting.reactor.ReactorActionManager

class Reactor1052002 extends SimpleReactor {
   def act() {
      rm.sprayItems(true, 1, 500, 1000, 15)
   }
}

Reactor1052002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1052002(rm: rm))
   return (Reactor1052002) getBinding().getVariable("reactor")
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