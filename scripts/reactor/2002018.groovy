package reactor


import scripting.reactor.ReactorActionManager


class Reactor2002018 {
   ReactorActionManager rm

   def act() {
      rm.sprayItems(true, 1, 100, 400, 15)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2002018 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2002018(rm: rm))
   return (Reactor2002018) getBinding().getVariable("reactor")
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