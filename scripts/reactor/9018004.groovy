package reactor


import scripting.reactor.ReactorActionManager


class Reactor9018004 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor9018004 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9018004(rm: rm))
   return (Reactor9018004) getBinding().getVariable("reactor")
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