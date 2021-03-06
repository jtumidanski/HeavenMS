package reactor


import scripting.reactor.ReactorActionManager

class Reactor2408004 extends SimpleReactor {
   def act() {
      rm.spawnNpc(2081008)
   }
}

Reactor2408004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2408004(rm: rm))
   return (Reactor2408004) getBinding().getVariable("reactor")
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