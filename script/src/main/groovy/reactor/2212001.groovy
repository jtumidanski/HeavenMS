package reactor


import scripting.reactor.ReactorActionManager


class Reactor2212001 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 80, 100)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2212001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2212001(rm: rm))
   return (Reactor2212001) getBinding().getVariable("reactor")
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