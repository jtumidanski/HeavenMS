package reactor


import scripting.reactor.ReactorActionManager

class Reactor9980000 extends SimpleReactor {
   def act() {
      rm.dispelAllMonsters((rm.getReactor().getName().substring(1, 2)).toInteger(), (rm.getReactor().getName().substring(0, 1)).toInteger())
   }
}

Reactor9980000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9980000(rm: rm))
   return (Reactor9980000) getBinding().getVariable("reactor")
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