package reactor


import scripting.reactor.ReactorActionManager


class Reactor2222000 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 80, 120)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2222000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2222000(rm: rm))
   return (Reactor2222000) getBinding().getVariable("reactor")
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