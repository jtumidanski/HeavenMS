package reactor


import scripting.reactor.ReactorActionManager


class Reactor2401002 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9300090)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor2401002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2401002(rm: rm))
   return (Reactor2401002) getBinding().getVariable("reactor")
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