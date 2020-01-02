package reactor


import scripting.reactor.ReactorActionManager


class Reactor1020000 {
   ReactorActionManager rm

   def act() {
      rm.warp(910200000, "pt00")
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor1020000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1020000(rm: rm))
   return (Reactor1020000) getBinding().getVariable("reactor")
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