package reactor


import scripting.reactor.ReactorActionManager

class Reactor3002001 extends SimpleReactor {
   def act() {
      rm.getEventInstance().showClearEffect(rm.getMap().getId())
      rm.dropItems()
   }
}

Reactor3002001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor3002001(rm: rm))
   return (Reactor3002001) getBinding().getVariable("reactor")
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