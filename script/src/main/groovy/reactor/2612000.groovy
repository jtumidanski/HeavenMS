package reactor


import scripting.reactor.ReactorActionManager


class Reactor2612000 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems()
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2612000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2612000(rm: rm))
   return (Reactor2612000) getBinding().getVariable("reactor")
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