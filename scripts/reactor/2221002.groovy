package reactor


import scripting.reactor.ReactorActionManager


class Reactor2221002 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(7130402, -340, 100)
      rm.mapMessage(5, "Here comes Green King Goblin!")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2221002 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221002(rm: rm))
   return (Reactor2221002) getBinding().getVariable("reactor")
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