package reactor


import scripting.reactor.ReactorActionManager

class Reactor1302000 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 8, 12, 2)
   }
}

Reactor1302000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1302000(rm: rm))
   return (Reactor1302000) getBinding().getVariable("reactor")
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