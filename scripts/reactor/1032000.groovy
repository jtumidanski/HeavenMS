package reactor


import scripting.reactor.ReactorActionManager


class Reactor1032000 {
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

Reactor1032000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1032000(rm: rm))
   return (Reactor1032000) getBinding().getVariable("reactor")
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