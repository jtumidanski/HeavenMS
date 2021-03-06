package reactor


import scripting.reactor.ReactorActionManager

class Reactor9980001 extends SimpleReactor {
   def act() {
      rm.dispelAllMonsters((rm.getReactor().getName().substring(1, 2)).toInteger(), (rm.getReactor().getName().substring(0, 1)).toInteger())
   }
}

Reactor9980001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9980001(rm: rm))
   return (Reactor9980001) getBinding().getVariable("reactor")
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