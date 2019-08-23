package reactor


import scripting.reactor.ReactorActionManager


class Reactor2201001 {
   ReactorActionManager rm

   def act() {
      for (int i = 0; i < 3; i++) {
         rm.spawnMonster(9300007)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2201001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2201001(rm: rm))
   return (Reactor2201001) getBinding().getVariable("reactor")
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