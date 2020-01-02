package reactor


import scripting.reactor.ReactorActionManager


class Reactor3009000 {
   ReactorActionManager rm

   def act() {
      if (rm.getReactor().getState() == ((byte) 4)) {
         rm.getEventInstance().showClearEffect(rm.getMap().getId())
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor3009000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor3009000(rm: rm))
   return (Reactor3009000) getBinding().getVariable("reactor")
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