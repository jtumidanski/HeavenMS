package reactor


import scripting.reactor.ReactorActionManager

class Reactor9102000 extends SimpleReactor {
   def act() {
      rm.dropItems(true, 2, 25, 100)
   }
}

Reactor9102000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9102000(rm: rm))
   return (Reactor9102000) getBinding().getVariable("reactor")
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