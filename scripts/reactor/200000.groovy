package reactor


import scripting.reactor.ReactorActionManager


class Reactor200000 {
   ReactorActionManager rm

   def act() {
      rm.dropItems()
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor200000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor200000(rm: rm))
   return (Reactor200000) getBinding().getVariable("reactor")
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