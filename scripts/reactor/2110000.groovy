package reactor


import scripting.reactor.ReactorActionManager


class Reactor2110000 {
   ReactorActionManager rm

   def act() {
      rm.playerMessage(5, "An unknown force has moved you to the starting point.")
      rm.warp(280010000, 0)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2110000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2110000(rm: rm))
   return (Reactor2110000) getBinding().getVariable("reactor")
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