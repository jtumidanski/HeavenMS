package reactor


import scripting.reactor.ReactorActionManager

class Reactor2202004 extends SimpleReactor {
   def act() {
      rm.sprayItems(true, 1, 30, 60, 15)
   }
}

Reactor2202004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2202004(rm: rm))
   return (Reactor2202004) getBinding().getVariable("reactor")
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