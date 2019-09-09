package reactor


import scripting.reactor.ReactorActionManager


class Reactor6102005 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 90, 360, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor6102005 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor6102005(rm: rm))
   return (Reactor6102005) getBinding().getVariable("reactor")
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