package reactor


import scripting.reactor.ReactorActionManager

class Reactor2002000 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 60, 80)
   }
}

Reactor2002000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2002000(rm: rm))
   return (Reactor2002000) getBinding().getVariable("reactor")
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