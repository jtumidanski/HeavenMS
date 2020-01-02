package reactor


import scripting.reactor.ReactorActionManager


class Reactor2002017 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 100, 400, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2002017 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2002017(rm: rm))
   return (Reactor2002017) getBinding().getVariable("reactor")
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