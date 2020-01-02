package reactor


import scripting.reactor.ReactorActionManager


class Reactor6742014 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 5, 25, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor6742014 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6742014(rm: rm))
   return (Reactor6742014) getBinding().getVariable("reactor")
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