package reactor


import scripting.reactor.ReactorActionManager


class Reactor9018003 {
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

Reactor9018003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor9018003(rm: rm))
   return (Reactor9018003) getBinding().getVariable("reactor")
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