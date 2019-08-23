package reactor


import scripting.reactor.ReactorActionManager


class Reactor8091000 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9400210, 2)
      rm.spawnMonster(9400209, 2)
      rm.mapMessage(5, "Some monsters are summoned.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor8091000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor8091000(rm: rm))
   return (Reactor8091000) getBinding().getVariable("reactor")
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