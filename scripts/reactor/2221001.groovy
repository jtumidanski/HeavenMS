package reactor


import scripting.reactor.ReactorActionManager


class Reactor2221001 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(7130401)
      rm.mapMessage(5, "Here comes Blue King Goblin!")
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2221001 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221001(rm: rm))
   return (Reactor2221001) getBinding().getVariable("reactor")
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