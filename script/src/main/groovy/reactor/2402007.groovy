package reactor


import scripting.reactor.ReactorActionManager


class Reactor2402007 {
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

Reactor2402007 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2402007(rm: rm))
   return (Reactor2402007) getBinding().getVariable("reactor")
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