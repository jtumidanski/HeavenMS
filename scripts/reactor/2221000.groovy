package reactor


import scripting.reactor.ReactorActionManager


class Reactor2221000 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(7130400)
      rm.mapMessage(5, "Here comes Yellow King Goblin!")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2221000 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221000(rm: rm))
   return (Reactor2221000) getBinding().getVariable("reactor")
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