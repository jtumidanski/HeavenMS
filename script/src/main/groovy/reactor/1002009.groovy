package reactor


import scripting.reactor.ReactorActionManager

class Reactor1002009 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 8, 15)
   }
}

Reactor1002009 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1002009(rm: rm))
   return (Reactor1002009) getBinding().getVariable("reactor")
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