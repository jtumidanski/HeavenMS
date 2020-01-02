package reactor


import scripting.reactor.ReactorActionManager


class Reactor2212004 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 80, 100)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2212004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2212004(rm: rm))
   return (Reactor2212004) getBinding().getVariable("reactor")
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