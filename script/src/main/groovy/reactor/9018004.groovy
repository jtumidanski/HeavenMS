package reactor


import scripting.reactor.ReactorActionManager

class Reactor9018004 extends SimpleReactor {
   def act() {

   }
}

Reactor9018004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9018004(rm: rm))
   return (Reactor9018004) getBinding().getVariable("reactor")
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