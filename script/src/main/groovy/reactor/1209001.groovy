package reactor


import scripting.reactor.ReactorActionManager

class Reactor1209001 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 8, 15, 1)
   }
}

Reactor1209001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1209001(rm: rm))
   return (Reactor1209001) getBinding().getVariable("reactor")
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