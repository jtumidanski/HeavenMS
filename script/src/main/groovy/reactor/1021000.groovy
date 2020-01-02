package reactor


import scripting.reactor.ReactorActionManager


class Reactor1021000 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9300091)
   }

   def hit() {

   }

   def touch() {

   }

   def release() {

   }
}

Reactor1021000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor1021000(rm: rm))
   return (Reactor1021000) getBinding().getVariable("reactor")
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