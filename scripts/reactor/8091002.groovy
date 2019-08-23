package reactor


import scripting.reactor.ReactorActionManager


class Reactor8091002 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9400213, 2)
      rm.spawnMonster(9400214, 2)
      rm.mapMessage(5, "Some monsters are summoned.")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor8091002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor8091002(rm: rm))
   return (Reactor8091002) getBinding().getVariable("reactor")
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