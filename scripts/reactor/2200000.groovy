package reactor


import scripting.reactor.ReactorActionManager


class Reactor2200000 {
   ReactorActionManager rm

   def act() {
      rm.playerMessage(5, "Gotcha! Try again next time!")
      rm.warp(221023200)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2200000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2200000(rm: rm))
   return (Reactor2200000) getBinding().getVariable("reactor")
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