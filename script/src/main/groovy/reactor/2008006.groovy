package reactor


import scripting.reactor.ReactorActionManager

class Reactor2008006 extends SimpleReactor {
   def act() {
      rm.getEventInstance().setProperty("statusStg3", "0")
   }
}

Reactor2008006 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2008006(rm: rm))
   return (Reactor2008006) getBinding().getVariable("reactor")
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