package reactor


import scripting.reactor.ReactorActionManager

class Reactor6802001 extends SimpleReactor {
   def act() {
      rm.sprayItems(true, 1, 100, 400, 15)
   }
}

Reactor6802001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6802001(rm: rm))
   return (Reactor6802001) getBinding().getVariable("reactor")
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