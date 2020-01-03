package reactor


import scripting.reactor.ReactorActionManager

class Reactor9201000 extends SimpleReactor {
   def act() {
      rm.spawnMonster(9300033, 8, -100, 50)
   }
}

Reactor9201000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9201000(rm: rm))
   return (Reactor9201000) getBinding().getVariable("reactor")
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