package reactor


import scripting.reactor.ReactorActionManager


class Reactor1012000 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 20, 40)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor1012000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1012000(rm: rm))
   return (Reactor1012000) getBinding().getVariable("reactor")
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