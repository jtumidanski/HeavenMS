package reactor


import scripting.reactor.ReactorActionManager


class Reactor2112000 {
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

Reactor2112000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2112000(rm: rm))
   return (Reactor2112000) getBinding().getVariable("reactor")
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