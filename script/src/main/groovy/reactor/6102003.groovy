package reactor


import scripting.reactor.ReactorActionManager

class Reactor6102003 extends SimpleReactor {
   def act() {
      rm.sprayItems(true, 1, 90, 360, 15)
   }
}

Reactor6102003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6102003(rm: rm))
   return (Reactor6102003) getBinding().getVariable("reactor")
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