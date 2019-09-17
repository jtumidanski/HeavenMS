package reactor


import scripting.reactor.ReactorActionManager


class Reactor1052001 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 500, 1000, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor1052001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1052001(rm: rm))
   return (Reactor1052001) getBinding().getVariable("reactor")
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