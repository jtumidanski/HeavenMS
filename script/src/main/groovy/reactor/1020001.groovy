package reactor


import scripting.reactor.ReactorActionManager


class Reactor1020001 {
   ReactorActionManager rm

   def act() {
      rm.warp(910200000, "pt01")
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor1020001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1020001(rm: rm))
   return (Reactor1020001) getBinding().getVariable("reactor")
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