package reactor


import scripting.reactor.ReactorActionManager


class Reactor2302000 {
   ReactorActionManager rm

   def act() {
      rm.dropItems(true, 2, 75, 90)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2302000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2302000(rm: rm))
   return (Reactor2302000) getBinding().getVariable("reactor")
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