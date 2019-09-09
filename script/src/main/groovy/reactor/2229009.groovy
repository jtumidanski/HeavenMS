package reactor


import scripting.reactor.ReactorActionManager


class Reactor2229009 {
   ReactorActionManager rm

   def act() {

   }

   def hit() {
      rm.hitMonsterWithReactor(6090003, 1)
   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2229009 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2229009(rm: rm))
   return (Reactor2229009) getBinding().getVariable("reactor")
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