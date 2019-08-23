package reactor


import scripting.reactor.ReactorActionManager


class Reactor2200002 {
   ReactorActionManager rm

   def act() {
      rm.mapMessage(5, "An unknown force has warped you into a trap.")
      rm.warpMap(922010201)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2200002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2200002(rm: rm))
   return (Reactor2200002) getBinding().getVariable("reactor")
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

def untouch() {
   getReactor().untouch()
}