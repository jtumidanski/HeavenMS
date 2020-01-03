package reactor


import scripting.reactor.ReactorActionManager

class Reactor2612002 extends SimpleReactor {
   def act() {
      rm.sprayItems()
   }
}

Reactor2612002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612002(rm: rm))
   return (Reactor2612002) getBinding().getVariable("reactor")
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