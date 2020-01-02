package reactor


import scripting.reactor.ReactorActionManager


class Reactor2511001 {
   ReactorActionManager rm

   def act() {
      for (int i = 0; i < 6; i++) {
         rm.spawnMonster(9300124)
         rm.spawnMonster(9300125)
      }
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2511001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2511001(rm: rm))
   return (Reactor2511001) getBinding().getVariable("reactor")
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

def release() {
   getReactor().release()
}