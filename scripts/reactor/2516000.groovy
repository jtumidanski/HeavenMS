package reactor


import scripting.reactor.ReactorActionManager


class Reactor2516000 {
   ReactorActionManager rm

   def act() {
      rm.mapMessage(5, "As Lord Pirate dies, Wu Yang is released!")
      rm.spawnNpc(2094001)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2516000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2516000(rm: rm))
   return (Reactor2516000) getBinding().getVariable("reactor")
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