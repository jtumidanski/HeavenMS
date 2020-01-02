package reactor


import scripting.reactor.ReactorActionManager


class Reactor2402008 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      rm.dropItems(true, 2, 5, 10, 1)
   }

   def touch() {

   }

   def release() {

   }
}

Reactor2402008 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2402008(rm: rm))
   return (Reactor2402008) getBinding().getVariable("reactor")
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