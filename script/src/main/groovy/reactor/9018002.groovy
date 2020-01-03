package reactor


import scripting.reactor.ReactorActionManager

class Reactor9018002 extends SimpleReactor {
   def act() {

   }
}

Reactor9018002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9018002(rm: rm))
   return (Reactor9018002) getBinding().getVariable("reactor")
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