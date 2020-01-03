package reactor


import scripting.reactor.ReactorActionManager

class Reactor2612004 extends SimpleReactor {
   def act() {

   }
}

Reactor2612004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612004(rm: rm))
   return (Reactor2612004) getBinding().getVariable("reactor")
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