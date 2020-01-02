package reactor


import scripting.reactor.ReactorActionManager


class Reactor1402000 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 8, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor1402000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1402000(rm: rm))
   return (Reactor1402000) getBinding().getVariable("reactor")
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