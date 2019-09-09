package reactor


import scripting.reactor.ReactorActionManager


class Reactor2119002 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      rm.hitMonsterWithReactor(6090000, 14)
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2119002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2119002(rm: rm))
   return (Reactor2119002) getBinding().getVariable("reactor")
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