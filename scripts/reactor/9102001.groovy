package reactor


import scripting.reactor.ReactorActionManager


class Reactor9102001 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 25, 100)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9102001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9102001(rm: rm))
   return (Reactor9102001) getBinding().getVariable("reactor")
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