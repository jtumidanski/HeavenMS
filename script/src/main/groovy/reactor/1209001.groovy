package reactor


import scripting.reactor.ReactorActionManager


class Reactor1209001 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 8, 15, 1)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor1209001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1209001(rm: rm))
   return (Reactor1209001) getBinding().getVariable("reactor")
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