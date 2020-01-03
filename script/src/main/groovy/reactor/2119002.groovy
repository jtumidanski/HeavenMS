package reactor


import scripting.reactor.ReactorActionManager

class Reactor2119002 extends SimpleReactor {
   def act() {

   }
}

Reactor2119002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2119002(rm: rm))
   return (Reactor2119002) getBinding().getVariable("reactor")
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