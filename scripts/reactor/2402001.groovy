package reactor


import scripting.reactor.ReactorActionManager


class Reactor2402001 {
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

Reactor2402001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2402001(rm: rm))
   return (Reactor2402001) getBinding().getVariable("reactor")
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