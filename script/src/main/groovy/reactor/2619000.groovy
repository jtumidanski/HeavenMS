package reactor


import scripting.reactor.ReactorActionManager


class Reactor2619000 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      rm.dropItems()
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2619000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2619000(rm: rm))
   return (Reactor2619000) getBinding().getVariable("reactor")
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