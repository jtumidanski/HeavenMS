package reactor


import scripting.reactor.ReactorActionManager


class Reactor9202012 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 30, 60, 10)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9202012 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9202012(rm: rm))
   return (Reactor9202012) getBinding().getVariable("reactor")
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