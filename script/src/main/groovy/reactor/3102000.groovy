package reactor


import scripting.reactor.ReactorActionManager

class Reactor3102000 extends SimpleReactor {
   def act() {
      rm.dropItems(false, 0, 0, 0, 3)
   }
}

Reactor3102000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor3102000(rm: rm))
   return (Reactor3102000) getBinding().getVariable("reactor")
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