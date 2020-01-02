package reactor


import scripting.reactor.ReactorActionManager


class Reactor2612005 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      if(rm.getReactor().getState() == ((byte) 4)) {
         rm.dropItems()
      }
   }

   def touch() {

   }

   def release() {

   }
}

Reactor2612005 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612005(rm: rm))
   return (Reactor2612005) getBinding().getVariable("reactor")
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