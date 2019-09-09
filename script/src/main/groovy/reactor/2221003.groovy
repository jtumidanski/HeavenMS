package reactor


import scripting.reactor.ReactorActionManager


class Reactor2221003 {
   ReactorActionManager rm

   def act() {
      rm.spawnMonster(9500400)
   }

   def hit() {

   }

   def touch() {

   }

   def untouch() {

   }
}

Reactor2221003 getReactor() {
   ReactorActionManager rm = (ReactorActionManager) getBinding().getVariable("rm")
   getBinding().setVariable("reactor", new Reactor2221003(rm: rm))
   return (Reactor2221003) getBinding().getVariable("reactor")
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