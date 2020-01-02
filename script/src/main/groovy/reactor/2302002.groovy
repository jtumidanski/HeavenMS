package reactor


import scripting.reactor.ReactorActionManager


class Reactor2302002 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 55, 70)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2302002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2302002(rm: rm))
   return (Reactor2302002) getBinding().getVariable("reactor")
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