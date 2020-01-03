package reactor


import scripting.reactor.ReactorActionManager

class Reactor2212000 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 80, 100)
   }
}

Reactor2212000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2212000(rm: rm))
   return (Reactor2212000) getBinding().getVariable("reactor")
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